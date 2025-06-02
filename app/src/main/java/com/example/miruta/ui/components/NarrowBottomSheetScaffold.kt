package com.example.miruta.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NarrowBottomSheetScaffold(
    showSheet: Boolean,
    scaffoldState: BottomSheetScaffoldState,
    sheetPeekHeight: Dp,
    sheetContent: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val sheetWidth = 375.dp

    LaunchedEffect(showSheet) {
        if (showSheet) {
            scaffoldState.bottomSheetState.expand()
        } else {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content(PaddingValues(0.dp))

        if (showSheet) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(sheetWidth)
            ) {
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = sheetPeekHeight,
                    sheetContainerColor = Color(0xFFF6F6F6),
                    sheetTonalElevation = 16.dp,
                    sheetShape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp),
                    sheetDragHandle = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 3.dp)
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(50)
                                    )
                            )

                        }
                    },
                    sheetContent = {
                        Box(
                            modifier = Modifier
                                .width(sheetWidth)
                                .padding(horizontal = 15.dp)
                                .navigationBarsPadding()
                                .imePadding()
                        ) {
                            sheetContent()
                        }
                        Spacer(modifier = Modifier.height(60.dp))
                    },
                    content = {}
                )
            }
        }
    }
}