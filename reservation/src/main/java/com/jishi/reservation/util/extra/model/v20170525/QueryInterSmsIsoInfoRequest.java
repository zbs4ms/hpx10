/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jishi.reservation.util.extra.model.v20170525;

import com.aliyuncs.RpcAcsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QueryInterSmsIsoInfoResponse;

/**
 * @author auto create
 * @version 
 */
public class QueryInterSmsIsoInfoRequest extends RpcAcsRequest<QueryInterSmsIsoInfoResponse> {
	
	public QueryInterSmsIsoInfoRequest() {
		super("Dysmsapi", "2017-05-25", "QueryInterSmsIsoInfo");
	}

	private String resourceOwnerAccount;

	private String countryName;

	private Long resourceOwnerId;

	private Long ownerId;

	public String getResourceOwnerAccount() {
		return this.resourceOwnerAccount;
	}

	public void setResourceOwnerAccount(String resourceOwnerAccount) {
		this.resourceOwnerAccount = resourceOwnerAccount;
		if(resourceOwnerAccount != null){
			putQueryParameter("ResourceOwnerAccount", resourceOwnerAccount);
		}
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
		if(countryName != null){
			putQueryParameter("CountryName", countryName);
		}
	}

	public Long getResourceOwnerId() {
		return this.resourceOwnerId;
	}

	public void setResourceOwnerId(Long resourceOwnerId) {
		this.resourceOwnerId = resourceOwnerId;
		if(resourceOwnerId != null){
			putQueryParameter("ResourceOwnerId", resourceOwnerId.toString());
		}
	}

	public Long getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
		if(ownerId != null){
			putQueryParameter("OwnerId", ownerId.toString());
		}
	}

	@Override
	public Class<QueryInterSmsIsoInfoResponse> getResponseClass() {
		return QueryInterSmsIsoInfoResponse.class;
	}

}
