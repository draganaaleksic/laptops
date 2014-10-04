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

(defroutes app-routes
  (GET "/login"
      [] (do (session-put! :login-error nil)
             (lc/is-logged-in (lv/home))))
  
  (POST "/login"
    request
    (do (lc/authenticate-user (:params request))
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
			                 (lv/home))))
   
   (GET "/calculate-price"
    []
    (session-put! :calculate-error nil)
    (session-put! :calculated-price nil)
    (lc/is-logged-in (lv/price-of-laptop)))
   
   (POST "/calculate-price"
    request
    (lc/is-logged-in (do (session-put! :calculate-error nil)
                       (session-put! :calculated-price nil)
                       (lc/calculate-price (:params request))
			                 (lv/price-of-laptop))))
   
   (GET "/logout"
    []
    (do (destroy-session!)
	  (lc/is-logged-in (lv/home))))
   
   (GET "/register"
    []
    (session-put! :error-register nil)
    (lc/is-not-logged-in (lv/register)))
   
   
   (POST "/save-user"
    request
    (lc/is-not-logged-in (do (session-put! :error-register nil)
                        (lc/save-user (:params request))
                        (lv/register))))
     
   (GET "/forgot-password"
    []
     (session-put! :check-email-error nil)
    (lc/is-not-logged-in (lv/forgot-password)))
     
   (POST "/send-mail"
    request
    (lc/is-not-logged-in (do (lc/check-email (:params request))
			                   (lv/forgot-password))))
     
   (GET "/delete-user"
    []
     (lc/is-logged-in (do (lc/delete-user (read-string (str (session-get :id))))
	                        (destroy-session!)
	                        (lv/home))))
      
   (GET "/edit-user"
      [] (do (session-put! :update-error nil)
    (lc/is-logged-in (lv/edit-user (n4j/read-node (session-get :id))))))
        
   (POST "/edit-user"
    request
    (lc/is-logged-in (do (session-put! :update-error nil)
                       (lc/update-user (:params request))
	                     (lv/edit-user (n4j/read-node (session-get :id))))))
   
   (GET "/all-laptops"
    []
    (lc/is-logged-in (do
                       (lc/all-laptops)
      (lv/all-laptops))))
   
   (GET "/update-laptop"
     request
      (do 
           ;(session-put! :update-error nil)
    (lc/is-logged-in (lv/edit-laptop (n4j/read-node (read-string (str (:id (:params request)))))))))
  
   (GET "/delete-laptop"
    request
     (lc/is-logged-in (do (lc/delete-laptop (read-string (str (:id (:params request)))))                    
	                        (lc/all-laptops)
                          (lv/all-laptops))))
   
   (POST "/edit-laptop"
    request
    (lc/is-logged-in (do ;(session-put! :update-error nil)
                       (lc/update-laptop (:params request))
	                     (lc/all-laptops)
                       (lv/all-laptops))))
   
  ; to serve static pages saved in resources/public directory
  (route/resources "/")
)

(def handler (-> (handler/site app-routes)
		 wrap-stateful-session
		 wrap-params
		 wrap-multipart-params))

(defn insert-defalut-nodes
  []
  (lc/default-nodes-in-database))

(defn run-server
  "Run jetty server"
  []
  (jetty/run-jetty handler {:port 5000 :join? false}))

