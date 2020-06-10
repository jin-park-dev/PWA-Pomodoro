(ns index-page
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [util.dev :refer [dev-panel]]
   [stylefy.core :as stylefy :refer [use-style]]
   [component.style :refer [fn-animate-css]]
   [state.index]))

; reagent doc - https://reagent-project.github.io/docs/master/reagent.core.html#var-create-class
; stylefy doc - https://jarzka.github.io/stylefy/doc/stylefy.core.html

;; stylefy tryout
;; 
;; (def button-style {:padding "25px"
;;                    :background-color "#BBBBBB"
;;                    :border "1px solid black"})

;; (defn animate-css-flip-in
;;   "Used for into title on Index Page"
;;   [css-atom]
;;   (fn-animate-css css-atom "animate__flipInX" nil 5000))


;; (defn- button-none-stylefy [text]
;;   [:div {:on-click #(.log js/console "Click! button-none-stylefy")
;;          :style {:height 55
;;                  :width 200
;;                  :border "solid"}
;;          :class "some-3rd-party-button-class"}
;;    text])

;; (defn- button-stylefy [text]
;;   [:div (use-style button-style {:on-click #(.log js/console "Click! button-stylefy")
;;                                  :class "some-3rd-party-button-class"})
;;    text])


(defn index-panel []
  (let [css-h1-intro (if @(rf/subscribe [:index-initial?])
                       (reagent/atom "animate__flipInX")
                       (reagent/atom nil)  ; ;or empty string better? ""
                       )]
    (fn []
      ;; (fn-animate-css css-h1-intro "animate__flipInX" nil 5000)
      [:div.flex-center.h-full
       [:h1.cursor-pointer.select-none.animate__animated {:class @css-h1-intro
                                                          :on-click (fn [] (fn-animate-css css-h1-intro "animate__flipInX" nil 1200))}
        "Welcome to PWAdoro"]
       [:p "Simple no nonsense Pomodoro Timer"]
       (when @(rf/subscribe [:dev?]) [dev-panel [css-h1-intro]])])))

(defn index-page-container []
  (reagent/with-let []
    [:main [index-panel]]
    (finally (rf/dispatch [:user-initial-visit false]))))


;; [:footer "The footer"]
