(ns util.time
  (:require
   [date-fns :as date-fns]))

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

(defn diff-in-duration
  "Takes time 1, time 2 and find difference. Returns hash-map in form of {:w wk :d d :h hr :m min :s sec}"
  [now start]
  (seconds->duration (date-fns/differenceInSeconds now start)))


;; pos? Misses 0. have to use "not neg" because of it, or between like below.
(defn humanize-double-digit
  "takes value and if it in single digit add, 0 in front.
  Only works for positive number.
  ;; nil, it gets accepts in (< -1 number 10) due to javascript. Due to this additional check.
  Returns string only to keep consistent unless nil."
  [number]
  (if (nil? number) ; nil is between -1 to 10 in javascript... o_O. Need additional check to not cast nil.
    number
    (if (< -1 number 10) ; 0-9 number only
      (str "0" number)
      (str number))
    ))