(ns work-in-progress.sampled
  (:use work-in-progress.audio-utils)
  (:import  [javax.sound.sampled
             AudioFormat
             AudioSystem
             BooleanControl
             CompoundControl
             EnumControl
             FloatControl
             Line
             Line$Info
             DataLine$Info
             Clip
             Mixer
             Port
             SourceDataLine
             TargetDataLine]))

;;;; AudioFormat

(def encodings ^:private
  (wrap-enum javax.sound.sampled.AudioFormat$Encoding))

(defn make-format
  "Create a new AudioFormat object from the given format info map."
  [format-info]
  (let  [{:keys  [encoding sample-rate sample-size-in-bits
                  channels frame-rate frame-size
                  endianness]} format-info]
    (AudioFormat.  (encodings encoding)
                  sample-rate
                  sample-size-in-bits
                  channels
                  frame-size
                  frame-rate
                  (= endianness :big-endian))))

(defn ->format
  "Gets the given object's AudioFormat."
  [o]
  (.getFormat o))

(defn ->format-info
  "Returns a map representing the given AudioFormat or object's
  AudioFormat properties."
  [o]
  (let  [fmt  (if  (isa?  (class o) AudioFormat)
                o
                (.getFormat o))]
    {:channels  (.getChannels fmt),
     :frame-rate  (.getFrameRate fmt),
     :frame-size  (.getFrameSize fmt),
     :sample-rate  (.getSampleRate fmt),
     :sample-size-in-bits  (.getSampleSizeInBits fmt),
     :encoding  (keyword  (clojurize-constant-name  (.getEncoding fmt)))
     :endianness  (if  (.isBigEndian fmt)
                    :big-endian
                    :little-endian)}))

(defn supports-conversion?
  "Check if the system supports data format conversion between the given
  source and target. The source must be an AudioFormat instance while
  the target can be another AudioFormat instance or an encoding. Note
  that if the source and target format are the same, false is returned."
  [source target]
  (let  [target  (if  (keyword? target)  (encodings target) target)]
    (AudioSystem/isConversionSupported target source)))

(defn convert
  "Convert the given audio stream to one with the specified audio
  format."
  [audio-stream target-fmt]
  (AudioSystem/getAudioInputStream target-fmt audio-stream))

;;;; Mixer

(def ^:dynamic *mixer* 
  "Mixer to be be used by functions creating lines, if nil let the
  system decides which mixer to use." nil)

(defn mixer-info
  "Returns a map of common properties for the given Mixer object."
  [mixer]
  (info->map  (.getMixerInfo mixer)))

;;;; Line

(def ^:private line-types 
  {:clip   Clip
   :input  TargetDataLine
   :output SourceDataLine
   :port   Port
   :mixer  Mixer})

(defn line-info  [line-type &  [fmt buffer-size]]
  (let  [line-type  (line-types line-type)]
    (cond  (and fmt buffer-size)
          (DataLine$Info. line-type fmt buffer-size)
          fmt      (DataLine$Info. line-type fmt)
          :default  (Line$Info. line-type))))

(defn make-line
  "Create a data line of the specified type (:clip, :output, :input)
  with an optional AudioFormat and buffer size."
  [line-type & [fmt buffer-size]]
  (let  [info  (line-info line-type fmt buffer-size)]
    (if *mixer*
      (.getLine *mixer* info)
      (AudioSystem/getLine info))))

(defmacro with-data-line
  "Open the given data line then, in a try expression, call start before
  evaluating body and call drain after. Finally close the line."
  [[binding make-line &  [fmt]] & body]
  `(let  [~binding #^DataLine ~make-line]
     ~(if fmt
        `(.open ~binding ~fmt)
        `(.open ~binding))
     (try
       (.start ~binding)
       (let  [result# (do ~@body)]
         (.drain ~binding)
         result#)
       (finally  (.close ~binding)))))

(def line-events ^:private
  (wrap-enum javax.sound.sampled.LineEvent$Type
             :constants-as-keys))

