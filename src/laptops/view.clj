(ns laptops.view
  (:use (sandbar stateful-session))
  (:require [laptops.neo4j :as n4j]
	    [laptops.html-generator :as hg]
	    [net.cgrand.enlive-html :as en]))

(en/deftemplate login
  (hg/build-html-page [{:temp-sel [:div.topcontent],
			:comp "public/pages/login.html",
			:comp-sel [:form#login-form]}])
  []
  [:title] (en/content "Login")
  [:div.topcontent] (en/set-attr :class "login")
  [:td#error-msgs] (if (not= (session-get :login-error) nil)                   
			  (en/content {:tag :div,
				       :attrs {:class "help"}
				       :content "Data not found"  })))

(en/deftemplate home
  (hg/build-html-page [{:temp-sel [:div.maincontent],
			:comp "public/pages/home.html",
			:comp-sel [:form#insert-laptop]}])
  []
  [:title] (en/content "Welcome")
  [:td#error-msgs] (if (not= (session-get :error-save-laptop) nil)
			  (en/content {:tag :div,
				       :attrs {:class "help"}
				       :content "Invalid input"  })
  (session-put! :error-save-laptop nil)))

(en/deftemplate price-of-laptop
  (hg/build-html-page [{:temp-sel [:div.maincontent],
			:comp "public/pages/laptop.html",
			:comp-sel [:div.laptop]}])
  []
  [:title] (en/content "Price")
  [:td#error-msgs] (if (not= (session-get :calculate-error) nil)                 
			  (en/content {:tag :div,
				       :attrs {:class "help"}
				       :content "Invalid input "  }))
  [:td#result] (if (not= (session-get :calculated-price) nil)               
       (en/content {:tag :input,
			     :attrs {:value (session-get :calculated-price)},
			     :content nil  })))


(en/deftemplate forgot-password
 (hg/build-html-page [{:temp-sel [:div.topcontent],
	:comp "public/pages/forgot-password.html",
		:comp-sel [:form#forgot-form]}])
 []
 [:title] (en/content "Forgot password")
 [:div.topcontent] (en/set-attr :class "forgot")
 [:td#error-msgs] (if (not= (session-get :check-email-error) nil)                  
		  (en/content {:tag :div,
			       :attrs {:class "help"}
			       :content "Email not found"  })))


(en/deftemplate register
  (hg/build-html-page [{:temp-sel [:div.topcontent],
			:comp "public/pages/register.html",
			:comp-sel [:form#register-form]}])
[]
  [:title] (en/content "Register user")
  [:div.topcontent] (en/set-attr :class "register")
  [:td#back] (en/content {:tag :a,
			     :attrs {:href "http://localhost:5000/login"},
			     :content "back to login"})
 [:td#error-msgs] (if (not= (session-get :error-register) nil)                 
		  (en/content {:tag :div,
			       :attrs {:class "help"}
			       :content "Invalid input"  })))


(en/deftemplate edit-user
  (hg/build-html-page [{:temp-sel [:div.maincontent],
			:comp "public/pages/register.html",
			:comp-sel [:form#register-form]}]) 
   [node]
  [:title] (en/content "Edit user")
  [:input#name] (en/set-attr :value (:name (:data node)))
  [:input#surname] (en/set-attr :value (:surname (:data node)))
  [:input#email] (en/set-attr :value (:email (:data node)))
  [:input#username] (en/set-attr :value (:username (:data node)))
  [:input#password] (en/set-attr :value (:password (:data node)))
  [:td#del] (en/content {:tag :a,
			     :attrs {:href "http://localhost:5000/delete-user"},
			     :content "Delete this account"})
  [:form#register-form] (en/set-attr :action "/edit-user") 
  [:td#error-msgs] (if (not= (session-get :update-error) nil)
		  (en/content {:tag :div,
			       :attrs {:class "help"}
			       :content "Invalid input"  })))


(en/defsnippet one-laptop-snippet "public/pages/all-laptops.html"
  [ [:div.one-laptop] ]
 [input]
 [:div.one-laptop] 
 (en/content { :tag :a,
			     :attrs {:href (str"http://localhost:5000/update-laptop?id="(:id input))
                :class "link"},
			     :content (:model input)}))

(en/deftemplate all-laptops 
  (hg/build-html-page [{:temp-sel [:div.maincontent],
	:comp "public/pages/all-laptops.html",
		:comp-sel [:div.a]}])
  []
  [:title] (en/content "All Posts")
  [:div.b] (en/content {:tag :p,
				       :content "Click on laptop to update it:"  })
  [:div.all-laptops] (en/content (map one-laptop-snippet (session-get :all-laptops))))

(en/deftemplate edit-laptop
 (hg/build-html-page [{:temp-sel [:div.maincontent],
			:comp "public/pages/home.html",
			:comp-sel [:form#insert-laptop]}])
   [node]
  [:title] (en/content "Edit laptop")
  [:input#model] (en/set-attr :value (:model (:data node)))
  [:input#screen-size] (en/set-attr :value "11")
  [:input#processor] (en/set-attr :value (:processor (:data node)))
  [:input#ram] (en/set-attr :value (:ram (:data node)))
  [:input#price] (en/set-attr :value (:price (:data node)))
  [:td#del] (en/content {:tag :a,
			     :attrs { :href (str"http://localhost:5000/delete-laptop?id="(:id node))},
			     :content "Delete this laptop"})
  [:form#insert-laptop] (en/set-attr :action (str"/edit-laptop?id="(:id node))))

