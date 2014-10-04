(ns laptops.validator
  (:require [valip.core :refer [validate]]
	    [valip.predicates :refer [present? matches email-address? decimal-string?]]))

(defn create-user-errors
  "Validate login form"
  [params]
  (validate params
	    [:name present? "Name can't be empty."]
	    [:surname present? "Surname can't be empty."]
	    [:email present? "Email can't be empty."]
	    [:email email-address? "Email not in valid format."]
	    [:password present? "Password can't be empty."]
	  	[:username present? "Username can't be empty."]))

(defn create-laptop-errors
  "Validate add laptop form"
  [params]
  (validate params
	    [:model present? "Model can't be empty."]
	    [:screen-size present? "Screen size can't be empty."]
      [:price present? "Price can't be empty."]
      [:price decimal-string? "Price can't be string."]
	    [:processor present? "Processor can't be empty."]
      [:processor decimal-string? "Processor can't be string."]
      [:ram present? "RAM can't be empty."]
      [:ram decimal-string? "RAM can't be string."]))

(defn calculate-price-errors
  "Validate calculate price form"
  [params]
  (validate params
	    [:processor present? "Processor can't be empty."]
      [:processor decimal-string? "Processor can't be string."]
      [:ram present? "RAM can't be empty."]
      [:ram decimal-string? "RAM can't be string."]))