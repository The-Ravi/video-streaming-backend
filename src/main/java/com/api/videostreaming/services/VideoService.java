package com.api.videostreaming.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.api.videostreaming.pojos.requests.MetadataRequest;
import com.api.videostreaming.pojos.requests.VideoRequest;
import com.api.videostreaming.pojos.responses.LoadVideoResponse;
import com.api.videostreaming.pojos.responses.MetadataResponse;
import com.api.videostreaming.pojos.responses.PlayVideoResponse;
import com.api.videostreaming.pojos.responses.PublishVideoResponse;
import com.api.videostreaming.pojos.responses.SearchVideoResponse;
import com.api.videostreaming.pojos.responses.SoftDeleteResponse;
import com.api.videostreaming.pojos.responses.VideoMetaDataResponse;

public interface VideoService {
    ResponseEntity<PublishVideoResponse> publishVideo(VideoRequest request);

    ResponseEntity<MetadataResponse> addOrEditVideoMetadata(Long videoId, MetadataRequest request);

    ResponseEntity<SoftDeleteResponse> softDeleteVideo(Long videoId);

    ResponseEntity<LoadVideoResponse> loadVideoContent(Long videoContentId);

    ResponseEntity<PlayVideoResponse> playVideoContent(Long videoId);

    ResponseEntity<List<SearchVideoResponse>> searchVideos(String searchPhrase, int page, int size);

    ResponseEntity<List<VideoMetaDataResponse>> getAllVideos(int page, int size);
    
}
