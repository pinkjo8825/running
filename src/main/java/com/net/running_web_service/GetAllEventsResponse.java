//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.11.28 at 12:50:01 AM ICT 
//


package com.net.running_web_service;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="runningEvent" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="runningEventName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="district" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="raceTypes"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="raceType" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="typeofEvent" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="prices"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="organization" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="activityArea" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="standard" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="level" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="startPeriod" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="rewards"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="reward" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "runningEvent"
})
@XmlRootElement(name = "getAllEventsResponse")
public class GetAllEventsResponse {

    protected List<GetAllEventsResponse.RunningEvent> runningEvent;

    /**
     * Gets the value of the runningEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the runningEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRunningEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GetAllEventsResponse.RunningEvent }
     * 
     * 
     */
    public List<GetAllEventsResponse.RunningEvent> getRunningEvent() {
        if (runningEvent == null) {
            runningEvent = new ArrayList<GetAllEventsResponse.RunningEvent>();
        }
        return this.runningEvent;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="runningEventName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="district" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="raceTypes"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="raceType" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="typeofEvent" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="prices"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="organization" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="activityArea" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="standard" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="level" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="startPeriod" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="rewards"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="reward" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "runningEventName",
        "district",
        "raceTypes",
        "typeofEvent",
        "prices",
        "organization",
        "activityArea",
        "standard",
        "level",
        "startPeriod",
        "rewards"
    })
    public static class RunningEvent {

        @XmlElement(required = true)
        protected String runningEventName;
        @XmlElement(required = true)
        protected String district;
        @XmlElement(required = true)
        protected GetAllEventsResponse.RunningEvent.RaceTypes raceTypes;
        @XmlElement(required = true)
        protected String typeofEvent;
        @XmlElement(required = true)
        protected GetAllEventsResponse.RunningEvent.Prices prices;
        @XmlElement(required = true)
        protected String organization;
        @XmlElement(required = true)
        protected String activityArea;
        @XmlElement(required = true)
        protected String standard;
        @XmlElement(required = true)
        protected String level;
        @XmlElement(required = true)
        protected String startPeriod;
        @XmlElement(required = true)
        protected GetAllEventsResponse.RunningEvent.Rewards rewards;

        /**
         * Gets the value of the runningEventName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRunningEventName() {
            return runningEventName;
        }

        /**
         * Sets the value of the runningEventName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRunningEventName(String value) {
            this.runningEventName = value;
        }

        /**
         * Gets the value of the district property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDistrict() {
            return district;
        }

        /**
         * Sets the value of the district property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDistrict(String value) {
            this.district = value;
        }

        /**
         * Gets the value of the raceTypes property.
         * 
         * @return
         *     possible object is
         *     {@link GetAllEventsResponse.RunningEvent.RaceTypes }
         *     
         */
        public GetAllEventsResponse.RunningEvent.RaceTypes getRaceTypes() {
            return raceTypes;
        }

        /**
         * Sets the value of the raceTypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link GetAllEventsResponse.RunningEvent.RaceTypes }
         *     
         */
        public void setRaceTypes(GetAllEventsResponse.RunningEvent.RaceTypes value) {
            this.raceTypes = value;
        }

        /**
         * Gets the value of the typeofEvent property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTypeofEvent() {
            return typeofEvent;
        }

        /**
         * Sets the value of the typeofEvent property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTypeofEvent(String value) {
            this.typeofEvent = value;
        }

        /**
         * Gets the value of the prices property.
         * 
         * @return
         *     possible object is
         *     {@link GetAllEventsResponse.RunningEvent.Prices }
         *     
         */
        public GetAllEventsResponse.RunningEvent.Prices getPrices() {
            return prices;
        }

        /**
         * Sets the value of the prices property.
         * 
         * @param value
         *     allowed object is
         *     {@link GetAllEventsResponse.RunningEvent.Prices }
         *     
         */
        public void setPrices(GetAllEventsResponse.RunningEvent.Prices value) {
            this.prices = value;
        }

        /**
         * Gets the value of the organization property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOrganization() {
            return organization;
        }

        /**
         * Sets the value of the organization property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOrganization(String value) {
            this.organization = value;
        }

        /**
         * Gets the value of the activityArea property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getActivityArea() {
            return activityArea;
        }

        /**
         * Sets the value of the activityArea property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setActivityArea(String value) {
            this.activityArea = value;
        }

        /**
         * Gets the value of the standard property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStandard() {
            return standard;
        }

        /**
         * Sets the value of the standard property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStandard(String value) {
            this.standard = value;
        }

        /**
         * Gets the value of the level property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLevel() {
            return level;
        }

        /**
         * Sets the value of the level property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLevel(String value) {
            this.level = value;
        }

        /**
         * Gets the value of the startPeriod property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStartPeriod() {
            return startPeriod;
        }

        /**
         * Sets the value of the startPeriod property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStartPeriod(String value) {
            this.startPeriod = value;
        }

        /**
         * Gets the value of the rewards property.
         * 
         * @return
         *     possible object is
         *     {@link GetAllEventsResponse.RunningEvent.Rewards }
         *     
         */
        public GetAllEventsResponse.RunningEvent.Rewards getRewards() {
            return rewards;
        }

        /**
         * Sets the value of the rewards property.
         * 
         * @param value
         *     allowed object is
         *     {@link GetAllEventsResponse.RunningEvent.Rewards }
         *     
         */
        public void setRewards(GetAllEventsResponse.RunningEvent.Rewards value) {
            this.rewards = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "price"
        })
        public static class Prices {

            protected List<String> price;

            /**
             * Gets the value of the price property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the price property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPrice().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getPrice() {
                if (price == null) {
                    price = new ArrayList<String>();
                }
                return this.price;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="raceType" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "raceType"
        })
        public static class RaceTypes {

            protected List<String> raceType;

            /**
             * Gets the value of the raceType property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the raceType property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getRaceType().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getRaceType() {
                if (raceType == null) {
                    raceType = new ArrayList<String>();
                }
                return this.raceType;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="reward" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "reward"
        })
        public static class Rewards {

            protected List<String> reward;

            /**
             * Gets the value of the reward property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the reward property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getReward().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getReward() {
                if (reward == null) {
                    reward = new ArrayList<String>();
                }
                return this.reward;
            }

        }

    }

}
