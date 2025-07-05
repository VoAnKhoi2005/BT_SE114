package com.example.bt4_23520790;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

interface ApiService {
    @GET("/api/v1/employees")
    Call<List<Employee>> getAll();
}
