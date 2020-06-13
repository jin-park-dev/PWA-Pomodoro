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
      (let [hover:bg-color (str "hover:" "bg-" color "-500")
            bg-color (if (= color @(rf/subscribe [:theme/general]) color)
                       (str "bg-" color "-500")
                       (str "bg-" color "-400")
                       )]
        (js/console.log "@subscription theme/general-text: " @(rf/subscribe [:theme/general-text 500]))
        (js/console.log "bg-color: " bg-color)
        (js/console.log "hover:bg-color: " hover:bg-color)
        (js/console.log "color: " color)
        ^{:key color} [:button.py-3.px-3.outline-none.focus:shadow-outline.text-white.rounded.mx-1
                       {:on-click (fn [e] (rf/dispatch [:theme/set-text-color color])
                                    )
                        :class (str
                                bg-color  ; default background color
                                " "
                                hover:bg-color)}])))])
