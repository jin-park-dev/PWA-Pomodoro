(ns mydev.dev)

(defn ^:dev/before-load stop []
  (js/console.log "stop"))

(defn ^:dev/after-load start []
  (js/console.log "start"))


(defn ssstart []
  (js/console.log "ssstart")
  (println "ssstart")
  )


#_(defn init []
  (js/self.addEventListener "message"
                            (fn [^js e]
                              (js/postMessage (.. e -data)))))



#_{
 :builds
 {:app
  {:target :browser
   :output-dir "public/js"
   :asset-path "/js"
   
   :modules
   {:shared
    {:entries []}
    :main
    {:init-fn my.app/init
     :depends-on #{:shared}}
    :worker
    {:init-fn my.app.worker/init
     :depends-on #{:shared}
     :web-worker true}}
   :devtools {:browser-inject :main}}}}

