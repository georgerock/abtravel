(ns abtravel.core
    (:require
   		[om.core :as om :include-macros true]
   		[om.dom :as dom :include-macros true]
        [goog.events :as events])
   	(:require-macros [cljs.core.async.macros :refer [go go-loop]])
   	(:import [goog.net XhrIo]
                goog.net.EventType
                [goog.events EventType]
                [goog.structs Map]
                [goog.Uri QueryData]))

(enable-console-print!)

(println "This text is printed from src/abtravel/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:buses []
                          :stations []
                          :lines []
                          :found-locations []}))

(defrecord Bus [id number coords])
(defrecord Station [id coords tags])


(extend-type js/NodeList
	ISeqable
	(-seq [array] (array-seq array 0)))

(extend-type js/FileList
	ISeqable
	(-seq [array] (array-seq array 0)))

(extend-type js/HTMLCollection
	ISeqable
    (-seq [array] (array-seq array 0)))

(defn json-xhr [{:keys [method url data on-complete]}]
	(let [xhr (XhrIo.)
		  content-type (if (= method "GET")
   						"application/json"
   						"application/x-www-form-urlencoded-data; charset=UTF8")]
		(events/listen xhr goog.net.EventType.COMPLETE
			(fn [e]
				(on-complete (js->clj (if (= method "GET")
										  (.getResponseJson xhr)
										  (.getResponse xhr))))))
		(. xhr
            (send url method (when data (-> (clj->js data) (Map.) (QueryData.createFromMap) (.toString)))
		 	    #js {"Content-Type" content-type}))))

(defn xhr-request [method url data on-complete]
(json-xhr
	{:method method
	 :url url
	 :data data
	 :on-complete on-complete}))

(defn get-something! []
    (xhr-request "GET" "http://172.17.0.2:5984" nil
        (fn [res]
            (js/console.log (clj->js res)))))

(defn location-component [data owner]
    (reify
        om/IRender
        (render [_]
            (dom/div #js {:className "row"}
                (dom/div #js {:className "col-sm-12"}
                    (dom/div #js {:className "panel panel-default"}
                        (dom/div #js {:className "panel-heading"} "Location 1")
                        (dom/div #js {:className "panel-body"}
                            (dom/div #js {:className "text-center"}
                                (dom/p nil "AB08STS - 5 mins")
                                (dom/p nil "You - 3 mins")
                                (dom/br nil)
                                (dom/div #js {:className "row"}
                                    (dom/div #js {:className "col-xs-6"}
                                        (dom/button #js {:className "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent"
                                                         :onClick #(get-something!)} "Buy Ticket"))
                                    (dom/div #js {:className "col-xs-6"}
                                        (dom/button #js {:className "mdl-button mdl-js-button mdl-button--raised mdl-button--colored"
                                                         :onClick #(js/window.alert "GO!")} "Go there")))))))))))

(defn search-component [data owner]
    (reify
        om/IRender
        (render [_]
            (dom/div #js {:className "row"}
                (dom/div #js {:className "col-sm-12"}
                    (dom/form #js {:action "#"}
                        (dom/div #js {:className "mdl-textfield mdl-js-textfield mdl-textfield--expandable"}
                            (dom/label #js {:className "mdl-buton mdl-js-button mdl-button--icon"
                                            :for "locationSearch"}
                                (dom/i #js {:className "material-icons"} "search"))
                            (dom/div #js {:className "mdl-textfield__expandable-holder"}
                                (dom/input #js {:className "mdl-textfield__input"
                                                :type "text"
                                                :id "locationSearch"})
                                (dom/label #js {:className "mdl-textfield__label"
                                                :for "expandableSearch"} "locationSearch")))))))))

(defn app-view [data owner]
    (reify
        om/IRender
        (render [_]
            (dom/div #js {:className "demo-card-wide mdl-card mdl-shadow--2dp"}
                (dom/div #js {:className "mdl-card__title"}
                    (dom/h2 #js {:className "mdl-card__title-text"} "ABTravel"))
                (dom/div #js {:className "mdl-card__supporting-text"}
                    "Va rugam introduceti locatia unde vreti sa ajungeti!")
                (dom/div #js {:className "mdl-card__actions mdl-card--border"}
                    (om/build search-component data))))))

(defn application [data owner]
	(reify
		om/IRender
		(render [_]
            (dom/div #js {:className "container-fluid"}
                (dom/div #js {:className "row"}
                    (dom/div #js {:className "col-xs-12"}
                    (om/build app-view data)))))))

(om/root application app-state
    {:target (. js/document (getElementById "app"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
