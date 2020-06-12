(ns component.clock
  (:require [reagent.core :as reagent]
            [stylefy.core :as stylefy :refer [use-style]]
            [date-fns :as date-fns]
            [util.time :refer [humanize-double-digit]]
            [component.style :refer [clock-styled-vue clock-styled-vue-item
                                     clock-digital-styled-vue--container-style]]))

; TOOD: All time components - ms-placement "right", due to flexbox rule will keep adjusting, aka timer shaking right and left. Using bottom only for now.

;; .text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100
(defn digital-clean
  "Clean style clock. Logic free. Container above should contain what time-now comes in here.
   Takes jsDate"
  [{:keys [time-now date-visible? ms-placement ms-visible? class]
    :or   {time-now (.now js/Date)  ; default of now, Not moving?
           date-visible? false
           ms-placement "bottom"
           ms-visible? false
           class nil}}]
  (let [_ms-placement (when ms-visible? ms-placement)]
    [:div.flex.flex-col.items-center.justify-center.content-center.self-center {:class class}
     (when date-visible? [:div.text-xl (date-fns/format time-now "y-MM-dd iii")])
     [:div.flex.flex-row.my-5
      [:div (date-fns/format time-now "h")]
      [:div ":"]
      [:div (date-fns/format time-now "mm")]
      [:div ":"]
      [:div.mr-5 (date-fns/format time-now "ss")]
      [:div (date-fns/format time-now "aaa")]
      (when (= _ms-placement "right") [:div.text-base (date-fns/format time-now "SSS")])]
     (when (= _ms-placement "bottom") [:div.text-base (date-fns/format time-now "SSS")])]))

(defn digital-vue
  "Vue style clock. Logic Free. Save architechture as digital-clock"
  [{:keys [time-now date-visible? ms-placement ms-visible? class]
    :or   {time-now (.now js/Date)  ; default of now, Not moving?
           date-visible? false
           ms-placement "bottom"
           ms-visible? false
           class nil}}]
  (let [_ms-placement (when ms-visible? ms-placement)]
    [:div.digital-vue (use-style clock-styled-vue)
     [:div.flex.flex-col.items-center.justify-center (use-style clock-styled-vue-item)
      (when date-visible? [:div.text-3xl (date-fns/format time-now "y-MM-dd iii")])
      [:div.flex.flex-row.text-6xl
       [:div (date-fns/format time-now "h")]
       [:div ":"]
       [:div (date-fns/format time-now "mm")]
       [:div ":"]
       [:div.mr-3 (date-fns/format time-now "ss")]
       [:div (date-fns/format time-now "aaa")]
       (when (= _ms-placement "right") [:div.text-base (date-fns/format time-now "SSS")])]
      (when (= _ms-placement "bottom") [:div.text-base (date-fns/format time-now "SSS")])]]))
