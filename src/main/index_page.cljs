(ns index-page
  (:require
   [reagent.core :as reagent]
   [stylefy.core :as stylefy :refer [use-style]]))

; reagent doc - https://reagent-project.github.io/docs/master/reagent.core.html#var-create-class
; stylefy doc - https://jarzka.github.io/stylefy/doc/stylefy.core.html


(def button-style {:padding "25px"
                   :background-color "#BBBBBB"
                   :border "1px solid black"})

(defn- button-none-stylefy [text]
  [:div {:on-click #(.log js/console "Click! button-none-stylefy")
         :style {:height 55
                 :width 200
                 :border "solid"}
         :class "some-3rd-party-button-class"}
   text])

(defn- button-stylefy [text]
  [:div (use-style button-style {:on-click #(.log js/console "Click! button-stylefy")
                                 :class "some-3rd-party-button-class"})
   text])

(defn index-panel []
  [:div
   [:h1 "index panel"]
   [button-none-stylefy "button-none-stylefy"]
   [button-stylefy "button-stylefy"]
   ])

(defn index-page-container []
  [:div
   [:h1 "index Container"]
   [index-panel]])
