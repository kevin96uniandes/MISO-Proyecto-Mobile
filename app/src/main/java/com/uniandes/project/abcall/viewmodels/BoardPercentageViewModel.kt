package com.uniandes.project.abcall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.models.Board
import com.uniandes.project.abcall.repositories.rest.BoardPercentageClient
import com.uniandes.project.abcall.repositories.rest.BoardSummaryClient

class BoardPercentageViewModel (
    private val boardPercentageClient: BoardPercentageClient
) : ViewModel() {

    private val _result = MutableLiveData<ApiResult<BoardPercentageClient.BoardPercentageResponse>>()
    val result: LiveData<ApiResult<BoardPercentageClient.BoardPercentageResponse>> get() = _result

    fun getBoardPercentage(boardPercentage: Board) {
        boardPercentageClient.getBoardPercentage(boardPercentage.toBoardPercentageRequestBody()) { response ->
            _result.value = response
        }
    }

}

private fun Board.toBoardPercentageRequestBody() =
    BoardPercentageClient.BoardPercentageRequestBody(
        channelId = this.channelId,
        stateId = this.stateId,
        startDate = this.startDate,
        endDate = this.endDate
    )