package com.matburt.mobileorg.Parsing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodePayload {

	private StringBuilder payload = new StringBuilder();
	
	private String content = null;
	private Date schedule = null;
	private Date deadline = null;
	
	private String nodeId = null;
	
	public void add(String line) {
		this.payload.append(line + "\n");
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContent() {
		if(this.content == null) {
			this.content = cleanPayload();
		}
		return this.content;
	}
		
	public String getNodeId() {
		if(this.nodeId == null)
			this.stripTags();
		
		return this.nodeId;
	}

	
	private String cleanPayload() {
		stripDate("SCHEDULED:");
		stripDate("DEADLINE:");

		stripTags();
		
		return payload.toString().trim();
	}
	
	private String stripDate(String scheduled) {
		int index = payload.indexOf(scheduled);
		
		if(index == -1)
			return null;
		
		int start = payload.indexOf("<", index);
		int end = payload.indexOf(">", start);
		
		String date = payload.substring(start + 1, end);
		
		payload.delete(index, end + 1);
		
		return date;
	}
	
	private void stripTags() {

		final Pattern propertiesLine = Pattern.compile(":[A-Za-z_]+:");
		Matcher propm = propertiesLine.matcher(this.payload);

		while(propm.find()) {
			String name = propm.group();
			
			int start = propm.start();
			int end = payload.indexOf("\n", propm.end());

			
			if(end == -1)
				end = propm.end();
			else {
				String value = payload.substring(propm.end(), end);
				if(name.equals(":ID:")) {
					this.nodeId = value.trim();
				}
			}
			payload.delete(start, end);
			propm = propertiesLine.matcher(this.payload);
		}
	}

//	private String getProperty(String name) {
//		String nameWithColon = ":" + name + ":";
//		int indexOfName = payload.indexOf(nameWithColon);
//		if (indexOfName != -1) {
//			String value = payload.substring(indexOfName + nameWithColon.length()).trim();
////			this.properties.put(name, value);
//			return value;
//		} else
//			return "";		
//	}
	
//	private String getOriginalId() {
//		this.nodeId = getProperty("ORIGINAL_ID");
//		return this.nodeId;
//	}

//	private String getId() {
//		//this.nodeId = getProperty("ID");
//		return this.nodeId;
//	}
//
//	private Date getDate(String name) {
//		if(payload.indexOf(name + ":") == -1)
//			return null;
//		
//		Pattern datePattern = Pattern
//				.compile("^.*" + name + ": <(\\S+ \\S+)( \\S+)?>");
//
//		Date date = null;
//		
//		try {
//			Matcher dateMatcher = datePattern.matcher(this.payload);
//			SimpleDateFormat dFormatter = new SimpleDateFormat("yyyy-MM-dd EEE");
//			if (dateMatcher.find())
//				date = dFormatter.parse(dateMatcher.group(1));
//		} catch (java.text.ParseException e) {}
//		
//		return date;
//	}
//	
//	private Date getDeadline() {
//		this.deadline = getDate("DEADLINE");
//		return this.deadline;
//	}
//	
//	private Date getScheduled() {
//		this.schedule = getDate("SCHEDULED");
//		return this.schedule;
//	}

	public String datesToString() {
		String dateInfo = "";

		SimpleDateFormat formatter = new SimpleDateFormat("<yyyy-MM-dd EEE>");
		if (this.deadline != null)
			dateInfo += "DEADLINE: " + formatter.format(this.deadline) + " ";

		if (this.schedule != null)
			dateInfo += "SCHEDULED: " + formatter.format(this.schedule) + " ";

		return dateInfo;
	}
}
