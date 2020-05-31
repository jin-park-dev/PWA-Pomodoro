(ns util.time
  (:require
   [clojure.string :as string]))

; https://www.geeksforgeeks.org/converting-seconds-into-days-hours-minutes-and-seconds/
; From https://rosettacode.org/wiki/Convert_seconds_to_compound_duration

(def seconds-in-minute 60)
(def seconds-in-hour (* 60 seconds-in-minute))
(def seconds-in-day (* 24 seconds-in-hour))
(def seconds-in-week (* 7 seconds-in-day))

(defn seconds->duration [seconds]
  (let [weeks   ((juxt quot rem) seconds seconds-in-week)
        wk      (first weeks)
        days    ((juxt quot rem) (last weeks) seconds-in-day)
        d       (first days)
        hours   ((juxt quot rem) (last days) seconds-in-hour)
        hr      (first hours)
        min     (quot (last hours) seconds-in-minute)
        sec     (rem (last hours) seconds-in-minute)]
    {:w wk :d d :h hr :m min :s sec}))

(comment
  (seconds->duration 66)
  (seconds->duration 66)
  )