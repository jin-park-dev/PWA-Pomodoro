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


; Clock-clean
(def clock-styled-vue {:font-family "'Share Tech Mono', monospace"
                       :color "#daf6ff"
                       :text-shadow "0 0 20px rgba(10, 175, 230, 1),  0 0 20px rgba(10, 175, 230, 0)"})

(def clock-styled-vue-item {; :grid-area "a"
                            :align-self "center"
                            :justify-self "center"})

; Based on https://codepen.io/gau/pen/LjQwGp
; Clock-vue
(def clock-digital-styled-vue--container-style {:height "400px"
                                                :background "radial-gradient(ellipse at center,  #0a2e38  0%, #000000 100%)"
                                                :background-size "100%"
                                                :border "solid"})