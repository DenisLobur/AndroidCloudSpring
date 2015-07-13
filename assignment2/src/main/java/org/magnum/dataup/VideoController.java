/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class VideoController {

	private static final String SERVER = "http://localhost:8080";

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "VideoController"
	 *
	 *
	 ________  ________  ________  ________          ___       ___  ___  ________  ___  __
	 |\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \
	 \ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_
	 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \
	 \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \
	 \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
	 \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|

	 *
	 */

/*	private List<Video> videos = new CopyOnWriteArrayList<Video>();

	@RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.GET)
	public @ResponseBody List<Video> getVideoList(){
		return videos;
	}

	@RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v) {
        Video video = new Video();
		video.setTitle(v.getTitle());
		video.setDuration(v.getDuration());
		video.setContentType(v.getContentType());
		video.setLocation(v.getLocation());
		video.setSubject(v.getSubject());
		video.setId(v.getId() > 0 ? v.getId() : UUID.randomUUID().variant());
		video.setDataUrl(v.getDataUrl() != null ? v.getDataUrl() : UUID.randomUUID().toString());

		videos.add(video);

		return video;

	}

	@RequestMapping(value = VIDEO_DATA_PATH, method = RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData(@Path(ID_PARAMETER) long id, @Part(DATA_PARAMETER) TypedFile videoData) {
        VideoStatus status = new VideoStatus(VideoStatus.VideoState.READY);
        Collection<Video> coll = videos;
        Video fakeVideo = new Video();
        fakeVideo.setId(id);
        if (!coll.contains(fakeVideo)) {
            return status;
        }
        return status;
	}


	@RequestMapping(value = VIDEO_DATA_PATH, method = RequestMethod.GET)
	public @ResponseBody Response getData(@Path(ID_PARAMETER) long id) {
		return new Response("", 200, "", new ArrayList<Header>(), null);
	}*/

	/**
	 * The data structure used to store videos
	 */
	private Map<Long,Video> videos = new HashMap<Long, Video>();

	/**
	 * A stream of id's
	 */
	private static final AtomicLong currentId = new AtomicLong(0L);


	/**
	 * A local debug flag
	 */
	private boolean DEBUG = true;

	////////////////////////////////////////////////////////////////////////////
	//				HELPER METHODS
	////////////////////////////////////////////////////////////////////////////

	/**
	 * A simple debugger
	 */
	private void debug(String s) {
		if (DEBUG) System.out.println(s);
	}


	/**
	 * Sets the id for a video if not already set
	 * @param entity  The Video to save
	 */
	private void checkAndSetId(Video entity) {
		if(entity.getId() == 0){
			entity.setId(currentId.incrementAndGet());
		}
	}

	/**
	 * Helper method to return the full data url for a video
	 *
	 * @param videoId  The id of the video for which you want data
	 * @return  The url to the video's data portion
	 */
	private String getDataUrl(long videoId){
		String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
		return url;
	}

	/**
	 * Helper method to return base url for the server
	 *
	 * @return  the base url for the servlet
	 */
	private String getUrlBaseForLocalServer() {
		HttpServletRequest request =
				((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String base =
				"http://"+request.getServerName()
						+ ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
		return base;
	}

	////////////////////////////////////////////////////////////////////////////
	//				MAIN API
	////////////////////////////////////////////////////////////////////////////

	/**
	 *  	Returns a list of stored videos
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody
	Collection<Video> getVideoList() {
		return videos.values();
	}

	/**
	 *
	 *  Adds a video, storing only it's meta data.
	 *
	 * @param aVideo The video object you want to store
	 * @return  The original video with its storage id and the url to its data
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video aVideo) {

		//Generate a unique ID
		checkAndSetId(aVideo);

		//Set the data url
		String dataUrl = this.getDataUrl(aVideo.getId());
		aVideo.setDataUrl(dataUrl);

		//Add to data structure
		debug("Add a video:" + aVideo);
		videos.put(aVideo.getId(), aVideo);
		return aVideo;

	}


	/**
	 *  Set data for a video.  The binary mpeg data for the video should be provided in
	 *  a multipart request as a part with the key "data".
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.POST)
	public @ResponseBody
	VideoStatus setVideoData(
			@PathVariable("id") long anId,
			@RequestPart(VideoSvcApi.DATA_PARAMETER) MultipartFile videoData,
			HttpServletResponse response) {

		VideoStatus result = null;

		debug("setting data for:" + anId);

		if(videos.containsKey(anId)) {

			Video theVideo = videos.get(anId);
			debug("updating data stream for:" + anId);

			//Save data
			try {
				VideoFileManager.get().saveVideoData(theVideo, videoData.getInputStream());
			} catch (IOException e) {
				System.err.println("Exception setting data for video: " +e);
				e.printStackTrace();
				response.setStatus(404);
			}
			result = new VideoStatus(VideoStatus.VideoState.READY);
			return result;

		} else {
			//Video not found...send response
			//response.sendError(404);
			response.setStatus(404);
		}
		return result;
	}


	/**
	 *  	Get data for a video
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method= RequestMethod.GET)
	public @ResponseBody
	void getVideoData(
			@PathVariable("id") long anId,
			HttpServletResponse response) {


		Video result = null;

		debug("Getting data for:" + anId );

		if(videos.containsKey(anId)) {
			result = videos.get(anId);
			debug("Copying video data to outputstream for video:" + anId + " with data url=" + result.getDataUrl());

			try {
				ServletOutputStream os =response.getOutputStream();
				VideoFileManager.get().copyVideoData(result, os);
				response.getOutputStream().close();

			} catch (IOException e) {
				System.err.println("Exception getting data for video: " +e);
				//e.printStackTrace();
				response.setStatus(404);
			}
		} else {
			//Video not found...send response
			response.setStatus(404);
		}
	}

}
