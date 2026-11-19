package id.navigo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.navigo.app.vm.ChatViewModel

@Composable
fun ChatScreen(vm: ChatViewModel, onBack: () -> Unit) {
    val msgs by vm.messages.collectAsStateWithLifecycle()
    val typing by vm.typing.collectAsStateWithLifecycle()
    val notice by vm.notice.collectAsStateWithLifecycle()
    val gate = rememberConsentGate()
    val list = rememberLazyListState()
    LaunchedEffect(msgs.size, typing) { if (msgs.isNotEmpty()) list.animateScrollToItem(msgs.size) }

    Screen("Chatbot Hukum", onBack, scroll = false) {
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), state = list, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (msgs.isEmpty()) item {
                NaviCard(Modifier.fillMaxWidth()) {
                    Text("Tanyakan seputar hukum bisnis: kontrak, hak cipta, merek, royalti, atau data pribadi klien.", fontSize = 13.sp)
                }
            }
            items(msgs, key = { it.id }) { m ->
                if (m.role == "user") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(m.text, color = Color.White, fontSize = 14.sp,
                            modifier = Modifier.widthIn(max = 280.dp).background(Primary, RoundedCornerShape(14.dp, 14.dp, 2.dp, 14.dp)).padding(12.dp))
                    }
                } else {
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.widthIn(max = 300.dp).background(Color.White, RoundedCornerShape(14.dp, 14.dp, 14.dp, 2.dp)).padding(12.dp)) {
                            Text(m.text, fontSize = 14.sp)
                            m.citation?.let { Spacer(Modifier.height(8.dp)); Pill("Rujukan: $it", PrimaryLight, Primary) }
                            Spacer(Modifier.height(6.dp))
                            Hint("Bukan nasihat hukum. Konsultasikan dengan ahli hukum.")
                        }
                    }
                }
            }
            if (typing) item { Text("• • •", color = Muted, modifier = Modifier.background(Color.White, RoundedCornerShape(14.dp)).padding(horizontal = 14.dp, vertical = 8.dp)) }
        }
        if (msgs.size < 2) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Apa itu royalti?", "Cek klausul NDA", "Syarat sah perjanjian").forEach {
                AssistChip(onClick = { vm.draft = it }, label = { Text(it, fontSize = 12.sp) })
            }
        }
        notice?.let { Text(it, color = RedFg, fontSize = 12.sp) }
        Row(Modifier.fillMaxWidth().imePadding(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(vm.draft, { vm.draft = it }, Modifier.weight(1f), shape = RoundedCornerShape(24.dp), maxLines = 3,
                placeholder = { Text("Tulis pertanyaan hukum…") })
            FilledIconButton(onClick = { gate { vm.send(vm.draft) } }, enabled = vm.draft.isNotBlank() && !typing, modifier = Modifier.size(46.dp), shape = CircleShape) {
                Icon(NIcons.Send, "Kirim")
            }
        }
    }
}
