package com.furqoncreative.githubusers2.data.entities.searchuser

import com.google.gson.annotations.SerializedName

data class SearchUserResponse(

	@field:SerializedName("total_count")
	val totalCount: Int? = null,

	@field:SerializedName("incomplete_results")
	val incompleteResults: Boolean? = null,

	@field:SerializedName("items")
	val items: List<GithubUser>? = null
)