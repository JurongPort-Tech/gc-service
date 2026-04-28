package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @Copyright 2001 Software Design and Consultancy Pte Ltd. All Rights Reserved.
 *            System Name : GBMS (General and Bulk Cargo Management Systems)
 *            Module : cargo - edo Component ID : EdoValueObject.java Component
 *            Description:
 *
 * @author
 * @version
 */

/*
 * Revision History ---------------- Author Request Number Description of Change
 * Version Date Released Creation 1.0 Vani Changed to add Stuff Indicator 1.3 03
 * Sept 2003 Zhengguo Deng add shutout cargo edo 08 June 2011 MCConsulting
 * Include EPC_IND deliveryTo EPC 1.5 15 Nov 2014 field in EDO for WWL logon MC
 * Consulting 23 JAN 2015, Added Billing accounts each for Wharfage and
 * Service/Others charges.
 */
@JsonInclude(value = Include.NON_NULL)
public class EdoValueObject implements Serializable

{}
