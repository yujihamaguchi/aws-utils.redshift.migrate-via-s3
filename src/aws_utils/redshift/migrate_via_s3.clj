(ns aws-utils.redshift.migrate-via-s3
  (:require [amazonica.core :refer :all]
            [amazonica.aws.s3 :as s3]
            [amazonica.aws.ec2 :as ec2]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clj-time.local :as tl]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [aws-utils.redshift.properties :refer :all])
  (:gen-class))

(def exit-code (atom 0))

(defn get-aws-default-credentials []
  (let [access "aws_access_key_id = "
        secret "aws_secret_access_key = "
        file   "/.aws/credentials"
        creds  (->> "user.home"
                    System/getProperty
                    ((fn [dir] (str dir file)))
                    slurp
                    (re-seq #"[^\r\n]+"))
        key-for (fn [k] (-> (filter #(.startsWith % k) creds)
                            first
                            (.replace k "")))]
    {:access-key (key-for access)
     :secret-key (key-for secret)}))

(defn get-local-date []
  (->> (tl/local-now)
       (tf/unparse (tf/formatter-local "yyyy/MM/dd HH:mm:ss"))))

(defn unload-redshift-data [src s3-working-dir credentials]
  (let [query-str (if (re-seq #"(?i)^SELECT.*FROM.*" src)
                    (clojure.string/replace src "'" "\\'")
                    (str "SELECT * FROM " src))]
    ; make sure the source table exists or the query is valid.
    (jdbc/query src-db [(str (clojure.string/replace query-str "\\'" "'") " LIMIT 1")])
    (jdbc/execute! src-db
      [(str "UNLOAD ('" query-str "') TO '" s3-working-dir "' CREDENTIALS '" credentials "' ESCAPE DELIMITER AS '\t' NULL AS '%NULL%' GZIP")])))

(defn clear-s3-path [s3-bucket-name s3-prefix]
  (when-let [keys (seq (map :key (:object-summaries (s3/list-objects :bucket-name s3-bucket-name :prefix s3-prefix))))]
    (s3/delete-objects
      {:bucket-name s3-bucket-name
       :keys (vec keys)})))

(defmacro doing [description expr]
  `(do
    (log/info "START:" ~description)
    ~expr
    (log/info "END  :" ~description)))

(defn migrate-via-s3 [src trgt opts]
  (let [local-date (get-local-date)
        s3-prefix (str "redshift_work/SNAPSHOT/" trgt "/" local-date)
        s3-working-dir (str "s3://" s3-bucket-name "/" s3-prefix)
        aws-cred (get-aws-default-credentials)
        credentials (str "aws_access_key_id=" (aws-cred :access-key) ";aws_secret_access_key=" (aws-cred :secret-key))
        src-subname (clojure.string/split (:subname src-db) #"/")
        src-cluster-name (first (re-seq #"^[^\.]+" (nth src-subname 2)))
        src-db-name (nth src-subname 3)
        trgt-subname (clojure.string/split (:subname trgt-db) #"/")
        trgt-cluster-name (first (re-seq #"^[^\.]+" (nth trgt-subname 2)))
        trgt-db-name (nth trgt-subname 3)]
    (try
      (doing (str "UNLOAD FROM '" src "' (cluster: " src-cluster-name ", database: " src-db-name ")")
             (unload-redshift-data src s3-working-dir credentials))

      ; make sure the target table exists.
      (jdbc/query trgt-db [(str "SELECT 1 FROM " trgt " LIMIT 1")])

      (when-not (= "yes" (opts "add"))
        (doing (str "TRUNCATE '" trgt "' (cluster: " trgt-cluster-name ", database: " trgt-db-name ")")
               (jdbc/execute! trgt-db [(str "TRUNCATE " trgt)])))

      (doing
        (str "COPY TO '" trgt "' (cluster: " trgt-cluster-name ", database: " trgt-db-name ")") 
        (jdbc/execute! trgt-db
          [(str "COPY " trgt " FROM '" s3-working-dir "' CREDENTIALS '" credentials "' ESCAPE DELIMITER AS '\t' NULL AS '%NULL%' ACCEPTANYDATE IGNOREBLANKLINES GZIP")]))

      (catch Exception e
        (log/error (.getMessage e))
        (reset! exit-code 1))
      (finally
        (when-not (= "yes" (opts "remain-archives"))
          (doing (str "CLEAN S3 OBJECTS '" s3-working-dir "'")
                 (clear-s3-path s3-bucket-name s3-prefix)))
        (System/exit @exit-code)))))

(defn -main [& args]
  (if-not (<= 2 (count args))
    (do
      (println "Wrong number of args (" (count args) ")")
      (reset! exit-code 1)
      (System/exit @exit-code))
    (migrate-via-s3 (first args)
                    (second args)
                    (when (drop 2 args) (apply hash-map (drop 2 args))))))

