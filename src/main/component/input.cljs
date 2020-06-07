(ns component.input
  (:require [reagent.core :as reagent]
            [stylefy.core :as stylefy :refer [use-style]]))


(defn number
  "Display only - Simple clean digital style
   Example of arguement - {:compound-duration {:h 0 :m 0 :s 0}}
   uses key and value to display.
   placement - right, bottom, nil
   "
  [{:keys [value handle-change handle+change class]
    :or   {value 0  ; default if no number
        ;;    
           handle-change (fn [] (js/console.log "handle-change pressed. Please add function"))
           handle+change (fn [] (js/console.log "handle+change pressed. Please add function"))
           class nil}}]
  (let []
    [:div.flex.flex-row {:class class}
     [:button#break-decrement.btn.btn-nav {:on-click handle-change} "-"]
     [:div#break-length.btn.btn-nav.clean-number-input.cursor-default.mx-4 value]
     [:button#break-increment.btn.btn-nav {:on-click handle+change} "+"]]))



     
;;   TODO: Very tempted to make it input but it requires custom validation for input to behave nice. Otherwise Right now we can add letters, etc
;;   :handle-value-change (fn [e] (swap! state assoc-in [:value-break] (-> e .-target .-value)))
;;   handle-value-change (fn [] (js/console.log "handle-value-change pressed. Please add function"))

#_[:div#break-length.btn.btn-nav.clean-number-input.mx-4
   {:type "number"
    :min min
    :max max
    :pattern "[0-9]+"
    :value value
    :on-change handle-value-change}]