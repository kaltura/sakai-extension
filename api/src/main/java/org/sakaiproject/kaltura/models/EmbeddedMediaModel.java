/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models;

import lombok.Data;

/**
 * EmbeddedMediaModel holds the contents of the Media Gallery form response
 * after a user selects a media item to embed.
 * @author mgillian
 *
 */
@Data
public class EmbeddedMediaModel {

	private String url;
	private String playerId;
	private String size;
	private String width;
	private String height;
	private String returnType;
	private String entryId;
	private String owner;
	private String title;
	private String duration;
	private String description;
	private String createdAt;
	private String tags;
	private String thumbnailUrl;
}
