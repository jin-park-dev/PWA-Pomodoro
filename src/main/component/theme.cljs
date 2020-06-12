(ns component.theme
  (:require [re-frame.core :as rf])
  )


;; Function baked in here, issue generating function.
(defn picker
  "Pick from hash-map
   {:css function} 
   "
  [colors]
  [:div.flex.flex-row
   (doall
    (for [color colors]
      ;;  ^{:key color} [:button.py-3.px-3.bg-gray-700 {:on-click (fn [e] (rf/dispatch [:theme/set-text-color color]))} color]
       ^{:key color} [:button.py-3.px-3.text-white.rounded.mx-1 {:on-click (fn [e] (rf/dispatch [:theme/set-text-color color]))
                                                    :class (str "bg-" color)}]
      ))])
