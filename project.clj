(defproject aws-utils.redshift.migrate-via-s3 "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :omit-source true
  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [amazonica "0.3.18"]
    [org.clojure/java.jdbc "0.3.6"]
    [postgresql/postgresql "8.4-702.jdbc4"]
    [clj-time "0.8.0"]
    [org.clojure/tools.logging "0.3.1"]
    [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                       javax.jms/jms
                                       com.sun.jdmk/jmxtools
                                       com.sun.jmx/jmxri]]
  ]
  :main aws-utils.redshift.migrate-via-s3)
