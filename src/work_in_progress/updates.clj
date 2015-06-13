(ns work-in-progress.updates
  (:require [quil.core :as q]))

(defn random-color []
  (q/color (q/random 30 255)
           (q/random 30 255)
           (q/random 30 255)))

(defn create-circle [i]
  {:type :circle
   :x (q/random (* i (/ (q/width) 20)) (* (inc i) (/ (q/width) 20)))
   :y (q/random (q/height))
   :radius (q/random 30 80)
   :border (q/random 5 12)
   :fill-color (random-color)
   :stroke-color (random-color)})

(defn state-zero [state]
  {:phase 0
   :items (map create-circle (range 20))})

(defn random-horisontal [y]
  (min (q/height) (max 0 (+ y (q/random -20 20)))))

(defn random-vertical [x]
  (min (q/width) (max 0 (+ x (q/random -20 20)))))

(defn random-move [shape]
  (-> shape
      (assoc :x (random-vertical (:x shape)))
      (assoc :y (random-horisontal (:y shape)))))

(defn shape-update [shape]
  (case (:type shape)
    :circle (random-move shape)
    :rect shape
    shape))

(defn update-zero [state]
  (assoc state :items (map shape-update (:items state))))

(defn state-one [state]
  {:phase 1
   :text ""
   :text-left "Xywzel presents: \n Work In Progress"
   :frame 0})

(defn update-one [state]
  (if (>= (:frame state) 15)
    (-> state
        (assoc :frame 0)
        (assoc :text (str (:text state) (first (:text-left state))))
        (assoc :text-left (rest (:text-left state))))
    (assoc state :frame (inc (:frame state)))))

(defn state-two [state]
  (let [s (q/load-shader "disco-shader.glsl")]
    (.set s "iResolution", (float (q/width)) (float (q/height)))
    {:phase 2
     :shader (q/load-shader "disco-shader.glsl")
     :box [{:x -1}]
     :effect 0}))

(defn update-two [state] state)

