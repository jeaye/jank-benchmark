(ns jank-benchmark.middleware
  (:require [ring.middleware
             [defaults :refer [secure-site-defaults wrap-defaults]]
             [json :refer [wrap-json-response]]
             [gzip :as gzip]]
            [config.core :refer [env]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn wrap-middleware [handler]
  (let [wrap-env (if (env :dev)
                   (comp wrap-exceptions wrap-reload)
                   (comp gzip/wrap-gzip))]
    (-> handler
        (wrap-defaults (-> secure-site-defaults
                           (assoc :cookies false)
                           (assoc :session {})
                           (assoc :proxy true)
                           (assoc-in [:static :resources] "/")
                           (assoc-in [:security :hsts] false)
                           (assoc-in [:security :anti-forgery] false)
                           (assoc-in [:security :ssl-redirect] false)))
        wrap-json-response
        wrap-env)))
