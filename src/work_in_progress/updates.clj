(ns work-in-progress.updates
  (:require [quil.core :as q]
            [work-in-progress.audio :as a]))

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
   :letter 0
   :text ""
   :text-left "Xywzel presents: \n Work In Progress"
   :frame 0})

(defn update-one [state]
  (if (>= (:frame state) 15)
    (do (a/play-back "resources/Boom-Kick.wav") 
        (-> state
            (assoc :frame 0)
            (assoc :letter (inc (:letter state)))
            (assoc :text (str (:text state) (first (:text-left state))))
            (assoc :text-left (rest (:text-left state)))))
    (assoc state :frame (inc (:frame state)))))

(defn state-two [state]
  (let [s (q/load-shader "resources/shader.glsl")]
    (.set s "iResolution" (float (q/width)) (float (q/height)))
    {:phase 2
     :shader s
     :x (/ (q/width) 2 )
     :y (/ (q/height) 2)
     :width 1
     :height 1}))

(defn update-two [state]
  (if (= (mod (q/frame-count) 40) 0) (a/play-back "resources/Bass.wav"))
  (if (= (mod (q/frame-count) 30) 0) (a/play-back "resources/Guitar.wav"))
  (-> state
      (random-move)
      (assoc :height (+ (:height state) (q/random 2 5)))
      (assoc :width (+ (:width state) (q/random 2 5)))))

(defn state-three [state]
  (let [s (q/load-shader "resources/box-shader.glsl")]
    (.set s "iResolution" (float (q/width)) (float (q/height)))
    (-> state
      (assoc :phase 3)
      (assoc :box-shader s)
      (assoc :box-size 150)
      (assoc :box-center [(/ (q/width) 2) (/ (q/height) 2) 0])
      (assoc :box-rotation 0)
      (assoc :frame 0))))

(defn box-move [tick]
  (let [x-start (/ (q/width) 2)
        y-start (/ (q/height) 2)]
   [(+ x-start (* 0.15 tick (Math/cos (* 0.05 tick))))
    (+ y-start (* 0.15 tick (Math/sin (* 0.05  tick))))] ) )

(defn update-three [state]
  (if (= (mod (q/frame-count) 30) 0) (a/play-back "resources/Guitar.wav"))
  (if (= (mod (q/frame-count) 60) 0) (a/play-back "resources/Organ-C3.wav"))
  (if (= (mod (+ (q/frame-count) 30) 60) 0) (a/play-back "resources/Organ-C4.wav"))
  (if (= (mod (q/frame-count) 30) 90) (a/play-back "resources/Organ-C7.wav")) 
  (-> state
      (assoc :box-size (+ (:box-size state) (q/random -1 1)))
      (assoc :box-rotation (+ (:box-rotation state) 0.080))
      (assoc :box-center (box-move (:frame state)))
      (assoc :frame (inc (:frame state)))))

(defn state-four [state]
  (assoc state
         :phase 4
         :wind 0.0
         :gravity 1.0
         :root {:dir 0
                :length 0
                :target-length 60
                :width 20
                :chields nil}))

(defn new-child [i width]
  {:dir (+ -0.5 (* i 0.25) (q/random -0.25 0.25))
   :length 0
   :target-length (+ 55 (q/random 10))
   :width (q/random (* 0.2 width) (* 0.9 width))})

(defn grow [branch]
  (if (> (:length branch) (:target-length branch))
    (if (:chields branch)
      (assoc branch :chields (map grow (:chields branch)))
      (assoc branch :chields (map #(new-child % (:width branch)) (range 4))))
   (assoc branch :length (+ 0.4 (:length branch)))))

(defn wind-change [x]
  (* 0.2 (Math/sin (* 0.0005 (q/millis)))))

(defn gravity-change [x]
  (+ x (* 0.01 (Math/sin (* 0.005 (q/millis))))))

(defn update-four [state]
  (if (= (mod (q/frame-count) 40) 0) (a/play-back "resources/Bass.wav"))
  (if (= (mod (q/frame-count) 80) 0) (a/play-back "resources/Organ-C3.wav"))
  (if (= (mod (+ (q/frame-count) 40) 80) 0) (a/play-back "resources/Organ-C4.wav"))
  (if (= (mod (q/frame-count) 40) 120) (a/play-back "resources/Organ-C7.wav")) 
  (-> state
    (assoc :root (grow (:root state)))
    (assoc :wind (wind-change (:wind state)))
    (assoc :gravity (gravity-change (:gravity state)))))

(defn phase-five [state]
  (assoc state :phase 5))

