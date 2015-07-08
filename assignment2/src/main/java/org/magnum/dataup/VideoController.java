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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class VideoController implements VideoSvcApi {

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

	private List<Video> videos = new CopyOnWriteArrayList<Video>();

	@RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.GET)
	public @ResponseBody List<Video> getVideoList(){
		return videos;
	}

	@RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@Body Video v) {
        Video video = Video.create().withContentType(v.getContentType())
                .withDuration(v.getDuration())
                .withSubject(v.getSubject())
                .withTitle(v.getTitle()).build();

		return video;

	}

	@Override
	public VideoStatus setVideoData(@Path(ID_PARAMETER) long id, @Part(DATA_PARAMETER) TypedFile videoData) {
		return null;
	}

	@Override
	public Response getData(@Path(ID_PARAMETER) long id) {
		return null;
	}
}
