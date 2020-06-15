(ns component.input
  (:require [reagent.core :as reagent]
            [component.style :refer [btn-invalid]]))


(defn number
  "Number with button to increase, decrease
   validation to enable, disable.
   "
  [{:keys [value id-value id-button id+button handle-change handle+change class validation-button validation+button]
    :or   {value 0  ; default if no number
           id-value nil
           id-button nil
           id+button nil
           handle-change (fn [] (js/console.log "handle-change pressed. Please add function"))
           handle+change (fn [] (js/console.log "handle+change pressed. Please add function"))
           class nil
           validation-button false  ; true if there is issue. Triggers button to be unclickable
           validation+button false  ; false when there is no issue.
           }}]
  (let [on-click (if validation-button
                   (merge {:on-click handle-change} btn-invalid)
                   {:on-click handle-change})
        on+click (if validation+button
                   (merge {:on-click handle-change} btn-invalid)
                   {:on-click handle+change})]
    [:div.flex.flex-row {:class class}
     [:button.btn.btn-nav (merge {:id id-button} on-click)
      "-"]
     [:div.btn.btn-nav.clean-number-input.cursor-default.mx-4 {:id id-value} value]
     [:button.btn.btn-nav (merge {:id id+button} on+click)
      "+"]]))

(defn title
  "Title for timers"
  [{:keys [value class on-change]
    :or {value nil
         class nil
         on-change nil}}]
  ;.bg-gray-200.hover:bg-teal-200   .hover:border-teal-300.focus:outline-none.focus:bg-teal-500.focus:shadow-outline.focus:border-gray-300
  [:input.text-center {:type "text"
                       :value value
                       :class class
                       :on-change on-change
                       :on-key-press
                       (fn [e]
                         (when (= (.-key e) "Enter")
                           (-> e .-target .blur)))}])


     
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