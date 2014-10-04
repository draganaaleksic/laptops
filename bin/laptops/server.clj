(ns laptops.server
  "Requests and responses on server"
  (:use compojure.core
	(sandbar stateful-session)
	[ring.middleware.params]
	[ring.middleware.multipart-params])
  (:require [compojure.handler :as handler]
	    [compojure.route :as route]
	    [laptops.view :as lv]
	    [laptops.controllor :as lc]
	    [laptops.neo4j :as n4j]
	    [ring.adapter.jetty :as jetty]))

;; defroutes macro defines a function that chains individual route
;; functions together. The request map is passed to each function in
;; turn, until a non-nil response is returned.
(defroutes app-routes
  (GET "/login"
      [] (do (println "Usao u GET log in- server.")
           (session-put! :login-error nil)
      (lc/is-logged-in (lv/home))))
  
  (POST "/login"
    request
    (do (lc/authenticate-user (:params request))
      (println "Usao u POST log in- server.")
        (lc/is-logged-in (lv/home))))
  
   (GET "/home"
    []
    (lc/is-logged-in (do
      (session-put! :error-save-laptop nil)
      (lv/home))))
   
   (POST "/save-laptop"
    request
    (lc/is-logged-in (do (session-put! :error-save-laptop nil)
                       (lc/save-laptop (:params request))
			 (lc/is-logged-in (lv/home)))))
   
   (GET "/calculate-price"
    []
    (println "Usao u calculate price - server.")
    (session-put! :calculate-error nil)
    (session-put! :calculated-price nil)
    (lc/is-logged-in (lv/price-of-laptop)))
   
   (POST "/calculate-price"
    request
    (lc/is-logged-in (do (session-put! :calculate-error nil)
                       (session-put! :calculated-price nil)
                       (lc/calculate-price (:params request))
			 (lc/is-logged-in (lv/price-of-laptop)))))
   
   (GET "/logout"
    []
    (do (destroy-session!)
	(lc/is-logged-in (lv/home))))
   
   (GET "/register"
    []
    ;(lc/is-logged-in (lv/home))
     
    ;(lv/register))
    (session-put! :error-register nil)
    (lc/is-not-logged-in (lv/register)))
   
   
     (POST "/save-user"
    request
 (do (println "Usao u save user- server.")
   (println request)
   (if (= (session-get :id) nil)
     ;;ako user nije ulogovan, radi se registrovanje
    (do (println "uradio registrovanje user-a")
 (lc/is-not-logged-in (do (session-put! :error-register nil)
                        (lc/save-user (:params request))
 ;(lc/is-logged-in (lv/home))
;(if (not= (session-get :error-register) nil)
 (lc/is-not-logged-in (lv/register))
 ;(lc/is-not-logged-in (lv/home)))
)))
     
    ;ako je user ulogovan, radi se update user-a
;  (lc/is-logged-in (do (lc/update-user(:params request))
   ;  (lc/is-logged-in (lv/edit-user))
    ; (println "uradio update user-a")
   ; ))
     
    
 ))
 )
     
     (GET "/forgot-password"
    []
    (println "Usao u forgot pass - server.")
     (session-put! :check-email-error nil)
    (lc/is-not-logged-in (lv/forgot-password)))
     
      (POST "/send-mail"
    request
    (lc/is-not-logged-in (do (lc/check-email (:params request))
			 (lc/is-not-logged-in (lv/forgot-password)))))
     
      (GET "/delete-user"
    []
    (do (lc/is-logged-in (lc/delete-user (read-string (str (session-get :id)))))
	(destroy-session!)
	(lc/is-logged-in (lv/home))))
      
        (GET "/edit-user"
      [] (do (println "Usao u GET edit user - server.")
           (session-put! :update-error nil)
    (lc/is-logged-in (lv/edit-user (n4j/read-node (session-get :id))))
      )
      )
   (POST "/edit-user"
    request
    (lc/is-logged-in (do (session-put! :update-error nil)
                       (lc/update-user (:params request))
			(lc/is-logged-in (lv/edit-user (n4j/read-node (session-get :id))))
    ))
    )
  
  ; to serve static pages saved in resources/public directory
  (route/resources "/")
  ; if page is not found
  ;(route/not-found (lv/page-not-found "Page not found"))
;  (GET "/:url/:id"
;    request
;    (println request))
;  (POST "/:url/:id"
;    request
;    (println request))
)

;; site function creates a handler suitable for a standard website,
;; adding a bunch of standard ring middleware to app-route:
(def handler (-> (handler/site app-routes)
		 wrap-stateful-session
		 wrap-params
		 wrap-multipart-params))

(defn run-server
  "Run jetty server"
  []
  (jetty/run-jetty handler {:port 5000 :join? false}))

