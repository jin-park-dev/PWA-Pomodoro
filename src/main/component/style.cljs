(ns component.style
  (:require [stylefy.core :as stylefy :refer [use-style]]))

(def btn-invalid {:disabled true :class "cursor-not-allowed"})

(defn fn-animate-css
  "Add css1
   Wait few second
   Replace with css2
   
   [Types]
   css-atom: atom
   css1: str
   css2: str
   length: int. 1000 = 1 second. In millisecond
   
   [Returns]
   nil
   "
  [css-atom css1 css2 length]
  (reset! css-atom css1)
  (js/setTimeout (fn [] (reset! css-atom css2)) length)
  )