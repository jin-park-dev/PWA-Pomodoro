(ns timer-page
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [state.subs :as sub]
   [clojure.string :refer [join]]
   [date-fns :as date-fns]
   [util.time :refer [seconds->duration diff-in-duration]]
   [util.dev :refer [dev-panel]]
   [component.timer :as clock]
   [component.input :as input]
   [component.icon :as icon]
   ))


(defn timer-simple []
  (reagent/with-let [state (reagent/atom {:start (.now js/Date)
                                          :now (.now js/Date)
                                          :pauses nil ; diff in seconds as vector
                                          ; :pauses [1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 1 4 5 1 2 3 31 6 21 424 516 66 ]

                                          :clean? true ; Inital state of running not have happened at all. E.g user interaction Clean
                                          :running? false

                                          :ms-visible? false
                                          :ms-placement "bottom"
                                          :dev? (rf/subscribe [:dev?])}) ; No button currently for dev?
                     timer-fn     (fn [] (js/setInterval
                                          (fn []
                                            (swap! state assoc-in [:now] (.now js/Date))) 70))

                     sum (fn [coll] (reduce + coll))  ; in seconds


                     title-atom (reagent/atom nil)
                     timer-id (reagent/atom nil)

                     ;;  Initial Start
                     fn-start (fn [e]
                                (swap! state assoc-in [:clean?] false)
                                (swap! state assoc-in [:running?] true)
                                (swap! state assoc-in [:now] (.now js/Date))
                                (swap! state assoc-in [:start] (.now js/Date))
                                (swap! timer-id timer-fn))

                     fn-reset (fn [e]
                                (js/clearInterval @timer-id)
                                (swap! state assoc-in [:clean?] true)
                                (swap! state assoc-in [:running?] false)
                                (swap! state assoc-in [:pauses] nil)
                                (swap! state assoc-in [:start] (.now js/Date))
                                (swap! state assoc-in [:end] (.now js/Date)))

                     fn-resume (fn [e]
                                 (swap! state assoc-in [:running?] true)
                                 (swap! state assoc-in [:start] (.now js/Date))
                                 (swap! state assoc-in [:now] (.now js/Date)) ; need, to stop flicking from timer-fn
                                 (swap! timer-id timer-fn))

                     fn-pause (fn [e]
                                (let [current-length (date-fns/differenceInSeconds (get-in @state [:now]) (get-in @state [:start]))]
                                  (js/clearInterval @timer-id)
                                  (swap! state update-in [:pauses] conj current-length)
                                  (swap! state assoc-in [:running?] false)
                                  (swap! state assoc-in [:start] (.now js/Date))))]  ;refreshed every 70ms. 1000ms = 1sec
    
    (let [pauses-sum (sum (get-in @state [:pauses]))
          duration-diff (date-fns/differenceInSeconds (get-in @state [:now]) (get-in @state [:start]))
          duration-diff-adjusted (+ duration-diff pauses-sum)
          compound-duration-all (seconds->duration duration-diff-adjusted)
          compound-duration-filtered (dissoc compound-duration-all :w :d)  ; Remove week, days. (Maybe add back if needed one day but it disables showing those two then.)
          ms (when (get-in @state [:running?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:now]) (get-in @state [:start])) 1000))
          compound-duration-plus-ms (assoc compound-duration-filtered :ms ms)
          
          clean? (get-in @state [:clean?])
          running? (get-in @state [:running?])
          
          display-compound-duration (cond
                              clean? {:h 0 :m 0 :s 0 :ms 0}
                              :else compound-duration-plus-ms
                              
                              )  ; Although component has default explictly choosing when on/off this way.
          
          previous-pause-length (sum (drop-last 1 (get-in @state [:pauses])))
          compound-duration-previous-pause-length (seconds->duration previous-pause-length)
          
          ]
      [:div.flex.flex-col.items-center.justify-center.content-center.self-center
       (let [class-bg @(rf/subscribe [:theme/general-bg 100])
             class-text @(rf/subscribe [:theme/general-text 400])
             hover (str "hover:" class-bg)
             class (join  " " ["mb-3" "rounded" #_"bg-gray-100" hover class-text])]
         [input/title {:value @title-atom
                       :class class
                       :on-change (fn [e] (reset! title-atom (-> e .-target .-value)))}])
       [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-opacity-100.cursor-pointer.select-none
        {:on-click #(swap! state update-in [:ms-visible?] not)}
        [clock/digital-clean {:compound-duration display-compound-duration
                              :ms-placement (get-in @state [:ms-placement])
                              :ms-visible? (get-in @state [:ms-visible?])}]]
       
       [:div.flex.flex-row.mt-10.text-xl.transition-25to100.opacity-50
        [:button#reset.btn.btn-nav.mr-8 {:on-click (fn [e] (fn-reset e))} [icon/stop]]
        [:button.btn.btn-nav {:on-click (fn [e] (if clean?
                                                  (fn-start e)
                                                  (if running? (fn-pause e) (fn-resume e))))}
         (if (get-in @state [:running?]) [icon/pause] [icon/play])]]
       
       (when (not clean?)
         [:div.mt-5
          [:div.text-center "Last Duration: " (if (= 0 previous-pause-length) "0s" [clock/digital-clean-text {:compound-duration compound-duration-previous-pause-length}])]
          [:div.text-center.my-3 "Pauses"]
          (into [:div.flex.flex-row.flex-wrap.max-w-xs]
                (map (fn [time] [clock/digital-clean-text {:compound-duration (seconds->duration time)}]) (get-in @state [:pauses])))
          #_(into [:div.flex.flex-row.flex-wrap.max-w-xs]
                (map (fn [time] [:div (str time ", ")]) (get-in @state [:pauses])))
          ])
       
       (when @(get-in @state [:dev?]) [dev-panel [state]])])
    (finally (js/clearInterval timer-fn))))

(defn timer-panel-nav []
  ; below is non-used attempt to auto add. User can press + and it will add new component (without destorying older)
  #_[:div.flex-center
   (doall
    (for [i (range 7)]
      [:div {:class "w-1/3"}
       [timer-simple]]))
   ]
  [:div.flex-center.h-full
   [timer-simple]]
  )

(defn timer-page-container []
  [:main [timer-panel-nav]])


(comment
  ; https://cljs.github.io/api/cljs.core/clj-GTjs
  (clj->js {"foo" 1 "bar" 2})
  (clj->js {:foo 1 :bar 2})
  (clj->js [:foo "bar" 'baz])
  (clj->js [1 {:foo "bar"} 4])

  (.stringify js/JSON (clj->js {:key "value"}))
  (.stringify js/JSON (clj->js (clj->js {"foo" 1 "bar" 2})))
  (.stringify js/JSON (clj->js (clj->js {:foo 1 :bar 2})))

  (js/console.log (clj->js {:foo 1 :bar 2})) ; So this does what I want! JS object json.

  (date-fns/formatDuration (clj->js {:months 1 :days 2}))
  (date-fns/formatDuration (clj->js {:seconds 55}))
  (date-fns/formatDuration (clj->js {:seconds 5555}))

  (date-fns/formatDuration (clj->js {:seconds 5555}) ["minutes" "seconds"])
  (date-fns/formatDuration (clj->js {:minutes 22 :seconds 5555}) ["minutes" "seconds"])

  (date-fns/formatDistance (.now js/Date) (.now js/Date))
  (date-fns/formatDistanceStrict (.now js/Date) (.now js/Date) (clj->js {:unit "minute"}))
  (date-fns/formatDistanceStrict (date-fns/addSeconds (.now js/Date) 123213) (.now js/Date) (clj->js {:unit "minute"}))
  (date-fns/formatDistanceStrict (date-fns/addSeconds (.now js/Date) 123213) (.now js/Date) (clj->js {:unit "second"}))

  (seconds->duration 56)
  (seconds->duration 60)
  (seconds->duration 61)
  (seconds->duration 3605))