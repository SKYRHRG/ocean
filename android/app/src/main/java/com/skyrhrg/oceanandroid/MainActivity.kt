package com.skyrhrg.oceanandroid

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var nameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var genderInput: EditText
    private lateinit var berthInput: EditText
    private lateinit var status: TextView
    private lateinit var prefs: SharedPreferences

    private val oceanBlue = Color.rgb(20, 78, 150)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("ocean_passenger_profile", Context.MODE_PRIVATE)
        buildUi()
        loadProfile()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(245, 247, 251))
        }

        val header = TextView(this).apply {
            text = "OCEAN Android"
            textSize = 22f
            setTextColor(Color.WHITE)
            setBackgroundColor(oceanBlue)
            setPadding(18, 18, 18, 18)
        }
        root.addView(header, LinearLayout.LayoutParams(-1, -2))

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 8, 12, 8)
        }
        nameInput = field("Passenger name")
        ageInput = field("Age")
        genderInput = field("Gender (M/F)")
        berthInput = field("Berth preference (optional)")
        panel.addView(nameInput)
        panel.addView(ageInput)
        panel.addView(genderInput)
        panel.addView(berthInput)

        val buttons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        buttons.addView(button("Save profile") { saveProfile() }, weightedParams())
        buttons.addView(button("Fill passenger form") { fillPassengerForm() }, weightedParams())
        panel.addView(buttons)

        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        actions.addView(button("Open IRCTC") { openIrctc() }, weightedParams())
        actions.addView(button("Reload") { webView.reload() }, weightedParams())
        panel.addView(actions)

        status = TextView(this).apply {
            text = "Ready. Sign in on IRCTC yourself, then use passenger autofill."
            textSize = 12f
            setTextColor(Color.DKGRAY)
            setPadding(4, 6, 4, 6)
        }
        panel.addView(status)

        val scroll = ScrollView(this).apply { addView(panel) }
        root.addView(scroll, LinearLayout.LayoutParams(-1, -2))

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url ?: return true
                    val host = url.host?.lowercase() ?: return true
                    if (host == "irctc.co.in" || host.endsWith(".irctc.co.in")) return false
                    Toast.makeText(this@MainActivity, "Only IRCTC pages are allowed in this browser.", Toast.LENGTH_SHORT).show()
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    status.text = "Page loaded. Passenger details are only filled when you tap the button."
                }
            }
            webChromeClient = WebChromeClient()
            setBackgroundColor(Color.WHITE)
        }
        root.addView(webView, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun field(hintText: String): EditText = EditText(this).apply {
        hint = hintText
        textSize = 14f
        isSingleLine = true
        setPadding(10, 4, 10, 4)
    }

    private fun button(label: String, action: () -> Unit): Button = Button(this).apply {
        text = label
        textSize = 11f
        setOnClickListener { action() }
    }

    private fun weightedParams() = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

    private fun saveProfile() {
        prefs.edit()
            .putString("name", nameInput.text.toString().trim())
            .putString("age", ageInput.text.toString().trim())
            .putString("gender", genderInput.text.toString().trim().uppercase())
            .putString("berth", berthInput.text.toString().trim())
            .apply()
        status.text = "Passenger profile saved on this device."
        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
    }

    private fun loadProfile() {
        nameInput.setText(prefs.getString("name", ""))
        ageInput.setText(prefs.getString("age", ""))
        genderInput.setText(prefs.getString("gender", ""))
        berthInput.setText(prefs.getString("berth", ""))
    }

    private fun openIrctc() {
        webView.loadUrl("https://www.irctc.co.in/nget/train-search")
        status.text = "Opening IRCTC. Login and security checks remain under your control."
    }

    private fun fillPassengerForm() {
        val name = nameInput.text.toString().trim()
        val age = ageInput.text.toString().trim()
        val gender = genderInput.text.toString().trim().uppercase()
        val berth = berthInput.text.toString().trim()
        if (name.isBlank() || age.isBlank() || gender !in listOf("M", "F", "T")) {
            Toast.makeText(this, "Enter name, age and gender (M/F/T).", Toast.LENGTH_LONG).show()
            return
        }
        val jsName = jsQuote(name)
        val jsAge = jsQuote(age)
        val jsGender = jsQuote(gender)
        val jsBerth = jsQuote(berth)
        val script = """
            (function() {
              const data = {name:$jsName, age:$jsAge, gender:$jsGender, berth:$jsBerth};
              const norm = s => (s || '').toLowerCase().replace(/[^a-z0-9]/g, '');
              function setValue(el, value) {
                if (!el || !value) return false;
                const proto = el.tagName === 'TEXTAREA' ? HTMLTextAreaElement.prototype : HTMLInputElement.prototype;
                const setter = Object.getOwnPropertyDescriptor(proto, 'value')?.set;
                if (setter) setter.call(el, value); else el.value = value;
                el.dispatchEvent(new Event('input', {bubbles:true}));
                el.dispatchEvent(new Event('change', {bubbles:true}));
                return true;
              }
              const fields = Array.from(document.querySelectorAll('input,select,textarea'));
              const find = (keys) => fields.find(el => {
                const label = norm((el.getAttribute('aria-label') || '') + ' ' +
                  (el.getAttribute('placeholder') || '') + ' ' + (el.name || '') + ' ' +
                  (el.id || '') + ' ' + (el.labels ? Array.from(el.labels).map(x => x.innerText).join(' ') : ''));
                return keys.some(k => label.includes(k));
              });
              let count = 0;
              const n = find(['passengername','fullname','psgnname','name']);
              const a = find(['passengerage','age']);
              const g = find(['passengergender','gender','sex']);
              const b = find(['berthpreference','berth']);
              if (n && setValue(n, data.name)) count++;
              if (a && setValue(a, data.age)) count++;
              if (g) {
                if (g.tagName === 'SELECT') {
                  const option = Array.from(g.options).find(o => norm(o.value) === norm(data.gender) || norm(o.text) === norm(data.gender) || (data.gender === 'M' && /male/.test(norm(o.text))) || (data.gender === 'F' && /female/.test(norm(o.text))));
                  if (option) { g.value = option.value; g.dispatchEvent(new Event('change',{bubbles:true})); count++; }
                } else if (setValue(g, data.gender)) count++;
              }
              if (b && data.berth) {
                if (b.tagName === 'SELECT') {
                  const option = Array.from(b.options).find(o => norm(o.text).includes(norm(data.berth)) || norm(o.value).includes(norm(data.berth)));
                  if (option) { b.value = option.value; b.dispatchEvent(new Event('change',{bubbles:true})); count++; }
                } else if (setValue(b, data.berth)) count++;
              }
              return count;
            })();
        """.trimIndent()
        webView.evaluateJavascript(script) { result ->
            val count = result?.trim('"')?.toIntOrNull() ?: 0
            status.text = if (count > 0) "Filled $count field(s). Review every field before continuing." else "No matching passenger fields found on this page."
            Toast.makeText(this, status.text, Toast.LENGTH_LONG).show()
        }
    }

    private fun jsQuote(value: String): String =
        "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\""

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }
}
