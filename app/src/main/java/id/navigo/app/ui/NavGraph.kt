package id.navigo.app.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.navigo.app.vm.*

@Composable
fun NavGraph() {
    val nav = rememberNavController()
    // ViewModel dibuat di level Activity agar state (hasil analisis, form dokumen, dll.) dibagi antar layar
    val auth: AuthViewModel = viewModel()
    val mou: MouViewModel = viewModel()
    val law: LawViewModel = viewModel()
    val chat: ChatViewModel = viewModel()
    val docs: DocViewModel = viewModel()
    val contracts: ContractViewModel = viewModel()
    val hist: HistoryViewModel = viewModel()
    val admin: AdminViewModel = viewModel()

    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    fun go(r: String) = nav.navigate(r) { launchSingleTop = true }
    fun tab(r: String) = nav.navigate(r) { popUpTo("home"); launchSingleTop = true }
    fun toAuth() = nav.navigate("auth") { popUpTo(0) }

    val showBar = route != null && route != "splash" && route != "auth" && !route.startsWith("admin")
    val selected = when {
        route == null -> "home"
        route.startsWith("mou") -> "mou"
        route.startsWith("uu") -> "uu"
        route == "chat" -> "chat"
        route.startsWith("doc") -> "docs"
        else -> "home"
    }

    Scaffold(bottomBar = { if (showBar) BottomBar(selected) { tab(it) } }) { pad ->
        NavHost(nav, "splash", Modifier.padding(pad).consumeWindowInsets(pad)) {
            composable("splash") {
                SplashScreen(auth) { loggedIn, isAdmin ->
                    nav.navigate(if (!loggedIn) "auth" else if (isAdmin) "admin" else "home") { popUpTo("splash") { inclusive = true } }
                }
            }
            composable("auth") {
                AuthScreen(auth) { isAdmin -> nav.navigate(if (isAdmin) "admin" else "home") { popUpTo("auth") { inclusive = true } } }
            }
            composable("home") { HomeScreen(auth, contracts, ::go) { auth.logout(); toAuth() } }

            composable("mou") { MouUploadScreen(mou, { tab("home") }) { go("mou_result") } }
            composable("mou_result") {
                MouResultScreen(mou, { nav.popBackStack() },
                    onSaveDeadline = {
                        val r = mou.state.value.result
                        contracts.prefill = Prefill(title = r?.title.orEmpty(), endDay = r?.endDay); go("contract_add")
                    }, onRevise = { go("docs") })
            }

            composable("uu") { LawSearchScreen(law, { tab("home") }) { go("uu_detail") } }
            composable("uu_detail") {
                LawDetailScreen(law, { nav.popBackStack() }) { q -> chat.newSession(); chat.draft = q; tab("chat") }
            }

            composable("chat") { ChatScreen(chat) { tab("home") } }

            composable("docs") { TemplatePickScreen(docs, { tab("home") }) { go("doc_form") } }
            composable("doc_form") { DocFormScreen(docs, { nav.popBackStack() }) { go("doc_preview") } }
            composable("doc_preview") { DocPreviewScreen(docs, { nav.popBackStack() }) { go("doc_ready") } }
            composable("doc_ready") {
                DocReadyScreen(docs, contracts, { tab("home") }, onDeadline = { go("contract_add") }, onHome = { tab("home") })
            }

            composable("contracts") { ContractDashboardScreen(contracts, { tab("home") }) { go("contract_add") } }
            composable("contract_add") { AddContractScreen(contracts, hist, { nav.popBackStack() }) { nav.navigate("contracts") { popUpTo("home") } } }

            composable("history") { HistoryScreen(hist, mou, chat, docs, contracts, { tab("home") }, ::go) }

            // ---- Admin ----
            composable("admin") { AdminDashboardScreen(admin, ::go) { auth.logout(); toAuth() } }
            composable("admin_laws") { AdminLawsScreen(admin) { nav.popBackStack() } }
            composable("admin_templates") { AdminTemplatesScreen(admin, { nav.popBackStack() }) { go("admin_template/$it") } }
            composable("admin_template/{id}", listOf(navArgument("id") { type = NavType.LongType })) {
                AdminTemplateEditScreen(admin, it.arguments?.getLong("id") ?: 0L) { nav.popBackStack() }
            }
            composable("admin_users") { AdminUsersScreen(admin, { nav.popBackStack() }) { go("admin_user/$it") } }
            composable("admin_user/{id}") {
                AdminUserDetailScreen(admin, it.arguments?.getString("id").orEmpty()) { nav.popBackStack() }
            }
        }
    }
}
