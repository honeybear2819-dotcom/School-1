package com.example.classroomcalendar

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var notice by remember { mutableStateOf("") }
            var title by remember {
                mutableStateOf("과제 제출")
            }
            var date by remember {
                mutableStateOf(
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Calendar.getInstance().time)
                )
            }
            var message by remember { mutableStateOf("") }

            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "클래스룸 일정 도우미",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        "공지나 이메일 내용을 붙여 넣고 " +
                        "일정을 검토해 보세요."
                    )

                    OutlinedTextField(
                        value = notice,
                        onValueChange = { notice = it },
                        label = { Text("공지 또는 이메일 내용") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )

                    Button(
                        onClick = {
                            val found = Regex(
                                """\d{4}-\d{2}-\d{2}"""
                            ).find(notice)

                            if (found != null) {
                                date = found.value
                                title = notice
                                    .lineSequence()
                                    .firstOrNull()
                                    ?.take(40)
                                    ?: "과제 제출"
                                message = "날짜 후보를 찾았어요. 확인해 주세요."
                            } else {
                                message = "날짜를 찾지 못했어요. 직접 입력해 주세요."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("일정 후보 분석")
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("일정 제목") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("날짜 (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(message)

                    Button(
                        onClick = {
                            try {
                                val parsed = SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.getDefault()
                                ).apply {
                                    isLenient = false
                                }.parse(date)

                                if (parsed == null) {
                                    message = "날짜 형식을 확인해 주세요."
                                } else {
                                    val start = Calendar.getInstance().apply {
                                        time = parsed
                                        set(Calendar.HOUR_OF_DAY, 9)
                                        set(Calendar.MINUTE, 0)
                                    }.timeInMillis

                                    val end = start + 60 * 60 * 1000

                                    val intent = Intent(
                                        Intent.ACTION_INSERT
                                    ).setData(
                                        CalendarContract.Events.CONTENT_URI
                                    ).putExtra(
                                        CalendarContract.Events.TITLE,
                                        title
                                    ).putExtra(
                                        CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                        start
                                    ).putExtra(
                                        CalendarContract.EXTRA_EVENT_END_TIME,
                                        end
                                    )

                                    startActivity(intent)
                                }
                            } catch (e: Exception) {
                                message = "날짜를 YYYY-MM-DD 형식으로 입력해 주세요."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("캘린더에서 확인 및 저장")
                    }

                    Text(
                        "현재 버전은 내용을 직접 붙여 넣는 방식입니다. " +
                        "Gmail과 Classroom 자동 연결은 아직 구현되지 않았습니다."
                    )
                }
            }
        }
    }
}