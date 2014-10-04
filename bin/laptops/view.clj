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
                     ;(do
			  ;(en/set-attr :id "error-msgs")
			  (en/content {:tag :div,
				       :attrs {:class "help"}
				       :content "Data not found"  })))

(en/deftemplate home
  (hg/build-html-page [{:temp-sel [:div.maincontent],
			:comp "public/pages/home.html",
			:comp-sel [:form#insert-laptop]}])
  []
  [:title] (en/content "Welcome")
  ; [:div.maincontent] (en/set-attr :class "home")
  [:td#error-msgs] (if (not= (session-get :error-save-laptop) nil)
                     ;(do
			 ; (en/set-attr :id "error-msgs")
			  (en/content {:tag :div,
				       :attrs {:class "help"}
				       :content "Invalid input"  })
    ; (println (str "greske suuuuuu" (session-get :error-save-laptop)))
   ; );(println (str "ime nije nil" (session-get :surname)))
     ) ;(println (str "greske su" (session-get :error-save-laptop)))
  (session-put! :error-save-laptop nil))

(en/deftemplate price-of-laptop
  (hg/build-html-page [{:temp-sel [:div.maincontent],
			:comp "public/pages/laptop.html",
			:comp-sel [:div.laptop]}])
  []
  [:title] (en/content "Price")
  [:td#error-msgs] (if (not= (session-get :calculate-error) nil)
                     ;(do
			  ;(en/set-attr :id "error-msgs")
    ;(session-put! :calculate-error nil)
			  (en/content {:tag :div,
				       :attrs {:class "help"}
				       :content "Invalid input "  })
     ;(println (str "ime je" (session-get :calculate-error)))
      
    ; )
     )
  [:td#result] (if (= (session-get :calculated-price) nil)
                    ; (do
			  ;(en/set-attr :id "error-msgs")
			  ;(en/content {:tag :input,
				;       :attrs {:value (session-get :calculated-price)}
				 ;      :content nil  })
     (println "cena jeste nil " (session-get :calculated-price))
    
     (do (println "cena nije nil " (session-get :calculated-price))
       (en/content {:tag :input,
			     :attrs {:value (session-get :calculated-price)},
			     :content nil  })
       )
     )
  )

(en/deftemplate register
  (hg/build-html-page [{:temp-sel [:div.topcontent],
			:comp "public/pages/register.html",
			:comp-sel [:form#register-form]}])
  ;:comp-sel [:div.edit-user]}])
[]
  ; [node]
  [:title] (en/content "Register user")
  [:div.topcontent] (en/set-attr :class "register")
  [:td#back] (en/content {:tag :a,
			     :attrs {:href "http://localhost:5000/login"},
			     :content "back to login"})
 ; [:div.script] (en/content {:tag :script,
	;		     :attrs {:src "http://localhost:5000/js/login.js"},
	;		     :content nil})
  ; (println "Uhvatio ovde 2.")
 ; [:div.script] (en/append {:tag :script,
	;	    :attrs nil,
	;	    :content "goodreads.login.jslogin_edit.init();"})
 [:td#error-msgs] (if (not= (session-get :error-register) nil)
                   ; (do  (println (str "ime je" (session-get :surname)))
		  ;(en/set-attr :id "error-msgs")
		  (en/content {:tag :div,
			       :attrs {:class "help"}
			       :content "Invalid input"  })
   
    )
  )

(en/deftemplate forgot-password
 (hg/build-html-page [{:temp-sel [:div.topcontent],
	:comp "public/pages/forgot-password.html",
		:comp-sel [:form#forgot-form]}])
 []
  [:title] (en/content "Forgot password")
 [:div.topcontent] (en/set-attr :class "forgot")
  [:td#error-msgs] (if (not= (session-get :check-email-error) nil)
                   ; (do  (println (str "ime je" (session-get :surname)))
		  ;(en/set-attr :id "error-msgs")
		  (en/content {:tag :div,
			       :attrs {:class "help"}
			       :content "Email not found"  })
   
    )(println (str "ime nije nil" (session-get :surname)))
    )

(en/deftemplate edit-user
  (hg/build-html-page [{:temp-sel [:div.maincontent],
			:comp "public/pages/register.html",
			:comp-sel [:form#register-form]}])
  ;:comp-sel [:div.edit-user]}])
;[]
   [node]
  [:title] (en/content "Edit user")
 ; [:div.topcontent] (en/set-attr :class "register")
  ;[:a#delete] (en/set-attr :value "Delete user-a")
  [:input#name] (en/set-attr :value (:name (:data node)))
  [:input#surname] (en/set-attr :value (:surname (:data node)))
  [:input#email] (en/set-attr :value (:email (:data node)))
  [:input#username] (en/set-attr :value (:username (:data node)))
  [:input#password] (en/set-attr :value (:password (:data node)))
  [:input#age] (en/set-attr :value (:age (:data node)))
  [:input#city] (en/set-attr :value (:city (:data node)))
  [:input#country] (en/set-attr :value (:country (:data node)))
  [:input#gender-male] (if (= (:gender (:data node)) "Male")
			  (en/set-attr :checked "checked")
			  (en/set-attr :name "gender"))
  [:input#gender-female] (if (= (:gender (:data node)) "Female")
			    (en/set-attr :checked "checked")
			    (en/set-attr :name "gender"))
  
  [:td#del] (en/content {:tag :a,
			     :attrs {:href "http://localhost:5000/delete-user"},
			     :content "Delete this account"})
  [:form#register-form] (en/set-attr :action "/edit-user")
  
  ;[:div.script] (en/content {:tag :script,
	;		     :attrs {:src "http://localhost:5000/js/login.js"},
	;		     :content nil})
   ;(println "Uhvatio ovde 2.")
  ;[:div.script] (en/append {:tag :script,
	;	    :attrs nil,
		;    :content "goodreads.login.jslogin_edit.init();"})
   [:td#error-msgs] (if (not= (session-get :update-error) nil)
                   ; (do  (println (str "ime je" (session-get :surname)))
		  ;(en/set-attr :id "error-msgs")
		  (en/content {:tag :div,
			       :attrs {:class "help"}
			       :content "Invalid input"  })
   
    )
  )

