package com.boot.todo.client;

import com.boot.todo.domain.ToDo;
import com.boot.todo.errors.ToDoErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dmytro Honchar
 * Date: 7/18/2022
 */
@Service
public class ToDoRestClient {
    private final RestTemplate restTemplate;
    private final ToDoRestClientProperties properties;

    @Autowired
    public ToDoRestClient(ToDoRestClientProperties properties){
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new ToDoErrorHandler());
        this.properties = properties;
    }

    public Iterable<ToDo> findAll() throws URISyntaxException {
        RequestEntity<Iterable<ToDo>> requestEntity = new RequestEntity<Iterable<ToDo>>(HttpMethod.GET,
                new URI(properties.getUrl() + properties.getBasePath()));
        ResponseEntity<Iterable<ToDo>> response =
                restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Iterable<ToDo>>(){});
        if(response.getStatusCode() == HttpStatus.OK){
            return response.getBody();
        }
        return null;
    }

    public ToDo findById(String id){
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        return restTemplate.getForObject(properties.getUrl() + properties.getBasePath() + "/{id}",
                ToDo.class,params);
    }

    public ToDo upsert(ToDo toDo) throws URISyntaxException {
        RequestEntity<?> requestEntity = new RequestEntity<>(toDo,HttpMethod.POST,
                new URI(properties.getUrl() + properties.getBasePath()));
        ResponseEntity<?> response = restTemplate.exchange(requestEntity,
                new ParameterizedTypeReference<ToDo>(){});
        if(response.getStatusCode() == HttpStatus.CREATED){
            return restTemplate.getForObject(response.getHeaders().getLocation(),ToDo.class);
        }
        return null;
    }

    public ToDo setCompleted(String id) throws URISyntaxException{
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        restTemplate.postForObject(properties.getUrl() + properties.getBasePath()
                + "/{id}?_method=patch",null, ResponseEntity.class, params);
        return findById(id);
    }

    public void delete(String id){
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        restTemplate.delete(properties.getUrl() + properties.getBasePath() + "/{id}",params);
    }
}
