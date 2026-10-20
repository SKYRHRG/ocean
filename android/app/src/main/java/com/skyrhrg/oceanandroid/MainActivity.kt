package com.skyrhrg.oceanandroid

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.content.SharedPreferences
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var root: LinearLayout
    private lateinit var dashboard: ScrollView
    private lateinit var webView: WebView
    private lateinit var browserBar: LinearLayout
    private lateinit var status: TextView
    private lateinit var from: EditText
    private lateinit var to: EditText
    private lateinit var date: EditText
    private lateinit var train: EditText
    private lateinit var coach: EditText
    private lateinit var quota: EditText
    private lateinit var name: EditText
    private lateinit var age: EditText
    private lateinit var gender: EditText
    private lateinit var berth: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private var browsing = false
    private val navy = Color.rgb(15, 35, 67)
    private val blue = Color.rgb(25, 91, 176)
    private val gold = Color.rgb(255, 174, 66)
    private val ink = Color.rgb(28, 42, 62)
    private val muted = Color.rgb(102, 117, 139)
    private val canvas = Color.rgb(244, 247, 252)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("ocean_profile_v2", Context.MODE_PRIVATE)
        buildUi()
        loadProfile()
    }

    private fun dp(n: Int) = (n * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(canvas) }
        val header = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(18), dp(20), dp(18)); setBackgroundColor(navy) }
        val brand = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = android.view.Gravity.CENTER_VERTICAL }
        brand.addView(TextView(this).apply {
            text = "O"; textSize = 25f; typeface = Typeface.DEFAULT_BOLD; gravity = android.view.Gravity.CENTER
            setTextColor(navy); setBackgroundColor(gold)
        }, LinearLayout.LayoutParams(dp(46), dp(46)))
        val brandWords = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, 0, 0) }
        brandWords.addView(TextView(this).apply { text = "OCEAN"; textSize = 25f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE) })
        brandWords.addView(TextView(this).apply { text = "SMART TRAIN COMPANION"; textSize = 10f; letterSpacing = 1.3f; setTextColor(Color.rgb(185, 205, 232)) })
        brand.addView(brandWords)
        header.addView(brand)
        header.addView(TextView(this).apply {
            text = "Plan your journey. Prepare passengers. Stay in control."
            textSize = 13f; setTextColor(Color.rgb(225, 234, 247)); setPadding(0, dp(14), 0, 0)
        })
        root.addView(header)

        browserBar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = android.view.Gravity.CENTER_VERTICAL; setPadding(dp(8), dp(4), dp(8), dp(4)); setBackgroundColor(Color.WHITE); visibility = View.GONE }
        browserBar.addView(button("← BACK", false) { showDashboard() }, LinearLayout.LayoutParams(-2, dp(44)))
        browserBar.addView(TextView(this).apply { text = "IRCTC BROWSER"; setTextColor(ink); textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setPadding(dp(12), 0, 0, 0) })
        root.addView(browserBar)

        dashboard = ScrollView(this)
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(14), dp(16), dp(24)) }
        body.addView(section("01", "Journey planner", "Enter journey details"))
        val journey = card()
        val routes = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        from = field("From station / code", "e.g. SDAH")
        to = field("To station / code", "e.g. NDLS")
        routes.addView(from, weighted()); routes.addView(to, weighted()); journey.addView(routes)
        date = field("Journey date", "DD-MM-YYYY")
        date.setText(SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date()))
        journey.addView(date)
        train = field("Train number / name (optional)", "Enter train details")
        journey.addView(train)
        val opts = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        coach = field("Travel class", "3A / SL / 2A")
        quota = field("Quota", "GENERAL")
        opts.addView(coach, weighted()); opts.addView(quota, weighted()); journey.addView(opts)
        body.addView(journey)

        body.addView(section("02", "Passenger profile", "Stored locally on this device"))
        val passenger = card()
        name = field("Full passenger name", "Passenger name"); passenger.addView(name)
        val person = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        age = field("Age", "Years"); gender = field("Gender", "M / F / T")
        person.addView(age, weighted()); person.addView(gender, weighted()); passenger.addView(person)
        berth = field("Berth preference (optional)", "LOWER / UPPER / etc."); passenger.addView(berth)
        phone = field("Mobile (optional)", "Contact number"); passenger.addView(phone)
        email = field("Email (optional)", "Email address"); passenger.addView(email)
        val saveRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        saveRow.addView(button("SAVE PROFILE", false) { saveProfile() }, weighted())
        saveRow.addView(button("CLEAR FORM", false) { clearProfile() }, weighted())
        passenger.addView(saveRow)
        val libraryRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        libraryRow.addView(button("SAVE PASSENGER", true) { savePassengerToLibrary() }, weighted())
        libraryRow.addView(button("CHOOSE SAVED", false) { chooseSavedPassenger() }, weighted())
        passenger.addView(libraryRow)
        body.addView(passenger)

        body.addView(section("03", "Booking workspace", "Security checks and payment stay under your control"))
        val actions = card()
        actions.addView(button("OPEN OFFICIAL IRCTC", true) { openIrctc() }, LinearLayout.LayoutParams(-1, dp(52)))
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row.addView(button("FILL PASSENGER FORM", false) { fillPassenger() }, weighted())
        row.addView(button("RELOAD PAGE", false) { if (browsing) webView.reload() else setStatus("Open IRCTC first to reload its page.") }, weighted())
        actions.addView(row); body.addView(actions)

        val statusCard = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(14), dp(12), dp(14), dp(12)); setBackgroundColor(Color.rgb(232, 240, 252)) }
        statusCard.addView(TextView(this).apply { text = "STATUS"; textSize = 10f; typeface = Typeface.DEFAULT_BOLD; setTextColor(blue); letterSpacing = 1f })
        status = TextView(this).apply { text = "Ready to plan. Fill in the journey and passenger details."; textSize = 13f; setTextColor(ink); setPadding(0, dp(5), 0, 0) }
        statusCard.addView(status); body.addView(statusCard)
        body.addView(TextView(this).apply {
            text = "OCEAN Android • Debug build\nThis companion does not bypass website security or guarantee a booking."
            textSize = 10f; setTextColor(muted); gravity = android.view.Gravity.CENTER; setPadding(0, dp(18), 0, 0)
        })
        dashboard.addView(body)
        root.addView(dashboard, LinearLayout.LayoutParams(-1, 0, 1f))

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val host = request?.url?.host?.lowercase() ?: return true
                    if (host == "irctc.co.in" || host.endsWith(".irctc.co.in")) return false
                    Toast.makeText(this@MainActivity, "Only IRCTC pages are allowed.", Toast.LENGTH_SHORT).show()
                    return true
                }
                override fun onPageFinished(view: WebView?, url: String?) { setStatus("IRCTC loaded. Review journey and passenger details before continuing.") }
            }
            setBackgroundColor(Color.WHITE); visibility = View.GONE
        }
        root.addView(webView, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun section(num: String, title: String, subtitle: String): View {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = android.view.Gravity.CENTER_VERTICAL; setPadding(0, dp(8), 0, dp(10)) }
        row.addView(TextView(this).apply { text = num; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; gravity = android.view.Gravity.CENTER; setTextColor(navy); setBackgroundColor(gold) }, LinearLayout.LayoutParams(dp(36), dp(36)))
        val labels = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(10), 0, 0, 0) }
        labels.addView(TextView(this).apply { text = title; textSize = 17f; typeface = Typeface.DEFAULT_BOLD; setTextColor(ink) })
        labels.addView(TextView(this).apply { text = subtitle; textSize = 11f; setTextColor(muted) })
        row.addView(labels); return row
    }

    private fun card() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; setPadding(dp(12), dp(8), dp(12), dp(12)); setBackgroundColor(Color.WHITE); elevation = dp(2).toFloat()
        layoutParams = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(18) }
    }

    private fun field(label: String, hintText: String) = EditText(this).apply {
        hint = hintText; contentDescription = label; textSize = 14f; isSingleLine = true
        setTextColor(ink); setHintTextColor(muted); setPadding(dp(8), dp(5), dp(8), dp(5))
        backgroundTintList = android.content.res.ColorStateList.valueOf(Color.rgb(205, 214, 226))
    }

    private fun button(label: String, primary: Boolean, action: () -> Unit) = Button(this).apply {
        text = label; textSize = 11f; typeface = Typeface.DEFAULT_BOLD
        setTextColor(if (primary) Color.WHITE else blue)
        backgroundTintList = android.content.res.ColorStateList.valueOf(if (primary) blue else Color.rgb(235, 241, 250))
        setOnClickListener { action() }
    }

    private fun weighted() = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = dp(5) }

    private fun saveProfile() {
        prefs.edit().putString("name", name.text.toString().trim()).putString("age", age.text.toString().trim())
            .putString("gender", gender.text.toString().trim().uppercase()).putString("berth", berth.text.toString().trim())
            .putString("phone", phone.text.toString().trim()).putString("email", email.text.toString().trim())
            .putString("from", from.text.toString().trim()).putString("to", to.text.toString().trim())
            .putString("date", date.text.toString().trim()).putString("train", train.text.toString().trim())
            .putString("class", coach.text.toString().trim()).putString("quota", quota.text.toString().trim()).apply()
        setStatus("Profile and journey preferences saved on this device.")
        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
        hideKeyboard()
    }

    private fun savePassengerToLibrary() {
        val passengerName = name.text.toString().trim()
        val passengerAge = age.text.toString().trim()
        val passengerGender = gender.text.toString().trim().uppercase()
        if (passengerName.isBlank() || passengerAge.toIntOrNull() == null || passengerAge.toInt() !in 0..120 || passengerGender !in listOf("M", "F", "T")) {
            Toast.makeText(this, "Enter a name, valid age and gender (M/F/T) first.", Toast.LENGTH_LONG).show()
            return
        }
        val people = try { JSONArray(prefs.getString("passenger_library", "[]") ?: "[]") } catch (_: Exception) { JSONArray() }
        val person = JSONObject()
            .put("name", passengerName)
            .put("age", passengerAge)
            .put("gender", passengerGender)
            .put("berth", berth.text.toString().trim())
            .put("phone", phone.text.toString().trim())
            .put("email", email.text.toString().trim())
        var replaced = false
        for (i in 0 until people.length()) {
            val old = people.optJSONObject(i) ?: continue
            if (old.optString("name").equals(passengerName, ignoreCase = true) && old.optString("age") == passengerAge) {
                people.put(i, person)
                replaced = true
                break
            }
        }
        if (!replaced) people.put(person)
        prefs.edit().putString("passenger_library", people.toString()).apply()
        setStatus(if (replaced) "Saved passenger profile updated on this device." else "Passenger added to this device's saved passenger library.")
        Toast.makeText(this, if (replaced) "Passenger updated" else "Passenger saved", Toast.LENGTH_SHORT).show()
        hideKeyboard()
    }

    private fun chooseSavedPassenger() {
        val people = try { JSONArray(prefs.getString("passenger_library", "[]") ?: "[]") } catch (_: Exception) { JSONArray() }
        if (people.length() == 0) {
            Toast.makeText(this, "No saved passengers yet. Fill the form and tap SAVE PASSENGER.", Toast.LENGTH_LONG).show()
            return
        }
        val labels = ArrayList<String>()
        for (i in 0 until people.length()) {
            val p = people.optJSONObject(i) ?: JSONObject()
            labels.add(p.optString("name", "Unnamed") + " • " + p.optString("age", "") + " • " + p.optString("gender", ""))
        }
        AlertDialog.Builder(this)
            .setTitle("Choose saved passenger")
            .setItems(labels.toTypedArray()) { _, which ->
                val p = people.optJSONObject(which) ?: return@setItems
                name.setText(p.optString("name"))
                age.setText(p.optString("age"))
                gender.setText(p.optString("gender"))
                berth.setText(p.optString("berth"))
                phone.setText(p.optString("phone"))
                email.setText(p.optString("email"))
                setStatus("Loaded " + p.optString("name") + " from the saved passenger library. Review details before use.")
            }
            .setNeutralButton("Delete all") { _, _ ->
                AlertDialog.Builder(this)
                    .setTitle("Delete saved passengers?")
                    .setMessage("This permanently deletes all saved passenger profiles from this device.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete") { _, _ ->
                        prefs.edit().remove("passenger_library").apply()
                        setStatus("Saved passenger library deleted.")
                    }
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadProfile() {
        name.setText(prefs.getString("name", "")); age.setText(prefs.getString("age", ""))
        gender.setText(prefs.getString("gender", "")); berth.setText(prefs.getString("berth", ""))
        phone.setText(prefs.getString("phone", "")); email.setText(prefs.getString("email", ""))
        from.setText(prefs.getString("from", "")); to.setText(prefs.getString("to", ""))
        date.setText(prefs.getString("date", SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())))
        train.setText(prefs.getString("train", "")); coach.setText(prefs.getString("class", "")); quota.setText(prefs.getString("quota", "GENERAL"))
    }

    private fun clearProfile() {
        listOf(name, age, gender, berth, phone, email).forEach { it.setText("") }
        setStatus("Passenger fields cleared. Saved preferences remain on this device.")
    }

    private fun openIrctc() {
        hideKeyboard()
        browsing = true; dashboard.visibility = View.GONE; browserBar.visibility = View.VISIBLE; webView.visibility = View.VISIBLE
        setStatus("Opening IRCTC. Sign in yourself and complete all official checks.")
        webView.loadUrl("https://www.irctc.co.in/nget/train-search")
    }

    private fun showDashboard() {
        browsing = false; webView.stopLoading(); webView.visibility = View.GONE
        browserBar.visibility = View.GONE; dashboard.visibility = View.VISIBLE; setStatus("Journey planner ready.")
    }

    private fun fillPassenger() {
        if (!browsing) { setStatus("Tap OPEN OFFICIAL IRCTC, sign in, and navigate to the passenger form first."); return }
        val n = name.text.toString().trim(); val a = age.text.toString().trim(); val g = gender.text.toString().trim().uppercase()
        if (n.isBlank() || a.toIntOrNull() == null || g !in listOf("M", "F", "T")) {
            Toast.makeText(this, "Enter passenger name, valid age and gender (M/F/T).", Toast.LENGTH_LONG).show(); return
        }
        val script = "(function(){const d={name:" + jsQuote(n) + ",age:" + jsQuote(a) + ",gender:" + jsQuote(g) + ",berth:" + jsQuote(berth.text.toString().trim()) + "};" +
            "const norm=s=>(s||'').toLowerCase().replace(/[^a-z0-9]/g,'');" +
            "function setv(e,v){if(!e||!v)return false;if(e.tagName==='SELECT'){const o=Array.from(e.options).find(x=>norm(x.value)===norm(v)||norm(x.text)===norm(v)||(v==='M'&&/male/.test(norm(x.text)))||(v==='F'&&/female/.test(norm(x.text))));if(!o)return false;e.value=o.value;}else{const p=e.tagName==='TEXTAREA'?HTMLTextAreaElement.prototype:HTMLInputElement.prototype;const s=Object.getOwnPropertyDescriptor(p,'value')?.set;if(s)s.call(e,v);else e.value=v;}e.dispatchEvent(new Event('input',{bubbles:true}));e.dispatchEvent(new Event('change',{bubbles:true}));return true;}" +
            "const fs=Array.from(document.querySelectorAll('input,select,textarea'));const find=ks=>fs.find(e=>{const l=norm((e.getAttribute('aria-label')||'')+' '+(e.getAttribute('placeholder')||'')+' '+(e.name||'')+' '+(e.id||' ')+' '+(e.labels?Array.from(e.labels).map(x=>x.innerText).join(' '):''));return ks.some(k=>l.includes(k));});let c=0;" +
            "if(setv(find(['passengername','fullname','psgnname','name']),d.name))c++;if(setv(find(['passengerage','age']),d.age))c++;if(setv(find(['passengergender','gender','sex']),d.gender))c++;if(d.berth&&setv(find(['berthpreference','berth']),d.berth))c++;return c;})()"
        webView.evaluateJavascript(script) { result ->
            val count = result?.trim('"')?.toIntOrNull() ?: 0
            setStatus(if (count > 0) "Filled $count field(s). Check every value yourself before continuing." else "No matching passenger fields found. Open the passenger details form and try again.")
            Toast.makeText(this, status.text, Toast.LENGTH_LONG).show()
        }
    }

    private fun jsQuote(s: String) = "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\""
    private fun hideKeyboard() { (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, 0) }
    private fun setStatus(s: String) { status.text = s }

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {
        if (browsing) { if (webView.canGoBack()) webView.goBack() else showDashboard() } else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) { webView.stopLoading(); webView.destroy() }
        super.onDestroy()
    }
}