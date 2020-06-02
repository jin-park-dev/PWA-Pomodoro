(ns pomodoro-page
  (:require
   [reagent.core :as reagent]
   [stylefy.core :as stylefy :refer [use-style]]
   [date-fns :as date-fns]
   [util.time :refer [seconds->duration]]
   [util.dev :refer [dev-panel]]))


(defn pomodoro-simple []
  (reagent/with-let [state (reagent/atom {:start (.now js/Date)
                                          ;; :now (date-fns/addMinutes (.now js/Date) 25)
                                          :now (date-fns/addSeconds (.now js/Date) 5)
                                          :ms? false
                                          :start? true
                                          :finished? false
                                          :dev? true}) ; No button currently for dev?
                     timer-fn     (js/setInterval
                                   (fn []
                                     (swap! state assoc-in [:start] (.now js/Date))
                                     (when (= 0 (date-fns/differenceInSeconds (get-in @state [:now]) (get-in @state [:start])))
                                       (swap! state assoc-in [:finished?] true)))
                                   70)]
    (let [compound-duration-all (seconds->duration (date-fns/differenceInSeconds (get-in @state [:now]) (get-in @state [:start])))
          compound-duration-filtered (dissoc compound-duration-all :w :d)
          compound-duration (cond
                              (get-in @state [:finished?]) {:h 0 :m 0 :s 0}
                              (get-in @state [:start?]) compound-duration-filtered
                              :else {:h 0 :m 25 :s 0})
          ms (when (get-in @state [:start?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:now]) (get-in @state [:start])) 1000))]
      [:div.flex.flex-col.items-center.justify-center.content-center.self-center
       [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100.cursor-pointer.select-none
        {:on-click #(swap! state update-in [:ms?] not)}
        (doall
         (for [[k v] compound-duration]
           ; Change original datastructure to nil to choose when to have number or now ?
           [:div.flex.flex-row.mr-2 {:key k} [:div v] [:div.text-base.self-end k]]))
        #_[:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms]] ; I like side but it keeps changing, due to flex being responsive and fontsize being different. Maybe float or span?
       (when (get-in @state [:ms?]) [:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms])
       [:div.flex.flex-row.mt-5.text-xl
        #_[:button.btn.btn-nav.mr-2 {:on-click (fn [e]
                                               (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 25))
                                               (swap! state assoc-in [:now] (date-fns/addMinutes (.now js/Date) 25)))}
         "Reset"]
        [:button.btn.btn-nav {:on-click (fn [e]
                                          (swap! state update-in [:start?] not)
                                          (swap! state assoc-in [:finished?] false)
                                          (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 25))
                                          (swap! state assoc-in [:now] (date-fns/addMinutes (.now js/Date) 25)))}
         (if (get-in @state [:start?]) "Stop" "Start")]]
       (when (get-in @state [:dev?]) [dev-panel [state]])])
    (finally (js/clearInterval timer-fn))))

(defn pomodoro-panel-nav []
  [:div.flex-center.h-full
   [pomodoro-simple]
   ])

(defn pomodoro-page-container []
  [:main [pomodoro-panel-nav]])
