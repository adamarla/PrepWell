package com.gradians.prepwell;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class QuestionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        builder = new QuestionBuilder(this.getApplicationContext());

        Question qsn1 = builder.build("11.246");
        renderQuestion(qsn1);
    }

    public void revealStep(View view) {
        ViewGroup llContainer = (ViewGroup)findViewById(R.id.llContainer);
        for (int i = 0; i < llContainer.getChildCount(); i++) {
            View v = llContainer.getChildAt(i);
            if (v.getVisibility() == View.INVISIBLE) {
                v.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void renderQuestion(Question question) {

        ViewGroup llContainer = (ViewGroup)findViewById(R.id.llContainer);

        String id = question.getId();
        String statement = question.getProblemStatement();

        //Problem
        llContainer.addView(getTextView("Q." + id));
        llContainer.addView(getTextView("Problem:"));
        if (question.getProblemStatement().startsWith("img"))
            llContainer.addView(getImageView("problems/" + id + "/" + statement));
        else
            llContainer.addView(getWebView(question.getProblemStatement()));

        //Solution - steps
        for (int i = 0; i < question.numSolutions(); i++) {
            llContainer.addView(getTextView(String.format("Solution %d", (i+1))));
            for (int j = 0; j < question.numParts(i); j++) {
                llContainer.addView(getTextView(String.format("Part %s)", (char)((int)'a'+j))));
                llContainer.addView(getWebView(question.getPartStatement(i, j)));
                for (int k = 0; k < question.numSteps(i, j); k++) {
                    String[] options = question.getOptions(i, j, k);
                    for (int l = 0; l < options.length; l++) {
                        llContainer.addView(getTextView(String.format("Step %s.%s", k, l)));
                        View display = null;
                        if (options[l].startsWith("img"))
                            display = getImageView("problems/" + id + "/" + options[l]);
                        else
                            display = getWebView(options[l]);
                        display.setVisibility(View.INVISIBLE);
                        llContainer.addView(display);
                    }
                }
            }
        }

        //Answers
        llContainer.addView(getTextView("Answers"));
        String[] answers = question.getAnswers();
        for (int i = 0; i < answers.length; i++) {
            char label = (char)((int)'a'+i);
            llContainer.addView(getTextView(label + ")"));
            View display = getWebView(answers[i]);
            display.setVisibility(View.INVISIBLE);
            llContainer.addView(display);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View getTextView(String text) {
        TextView tv = new TextView(getApplicationContext());
        tv.setTextColor(Color.BLACK);
        tv.setText(text);
        return tv;
    }

    private View getImageView(String img) {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(img);
        } catch (Exception e) { }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);

        ImageView iv = new ImageView(getApplicationContext());
        iv.setImageBitmap(bitmap);
        return iv;
    }

    private View getWebView(String latex) {
        final WebView webView = new WebView(getApplicationContext());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    webView.loadUrl("javascript:MathJax.Hub.Queue(['Typeset', MathJax.Hub]);");
                } else {
                    webView.evaluateJavascript("MathJax.Hub.Queue(['Typeset', MathJax.Hub]);", null);
                }
            }
        });
        webView.setBackgroundColor(Color.WHITE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            webView.getSettings().setDisplayZoomControls(false);
        webView.loadDataWithBaseURL("file:///android_asset/mathjax-svg",
                String.format(HTML, latex), "text/html", "utf-8", null);
        return webView;
    }

    QuestionBuilder builder;

    private final String
            HTML = "<html><head>"
            + "<script type='text/x-mathjax-config'>"
            +   "MathJax.Hub.Config({ "
            +     "showMathMenu: false,"
            +     "jax: [\"input/TeX\", \"output/SVG\"],"
            +     "extensions: [\"tex2jax.js\",\"mml2jax.js\",\"MathMenu.js\",\"MathZoom.js\"],"
            +     "TeX: { extensions: [\"AMSmath.js\", \"AMSsymbols.js\", \"noErrors.js\", \"noUndefined.js\"] },"
            +     "SVG: { "
            +       "scale: 100,"
            +       "font: \"TeX\", "
            +       "linebreaks: { automatic: false, width: \"automatic\" }, "
            /*
            +       "styles: { "
            +         "\".MathJax_SVG svg > g, .MathJax_SVG_Display svg > g\": {"
            +           "fill: \"#FFF\","
            +           "stroke: \"#FFF\""
            +         "}"
            +       "}"
            */
            +     "}"
            +   "});"
            + "MathJax.Hub.Register.StartupHook(\"SVG Jax Ready\", function() {"
            +   "var VARIANT = MathJax.OutputJax[\"SVG\"].FONTDATA.VARIANT;"
            +   "VARIANT[\"normal\"].fonts.unshift(\"MathJax_SansSerif\");"
            + "});"
            + "</script>"
            + "<script type='text/javascript' src='file:///android_asset/mathjax-svg/MathJax.js'></script>"
            + "</head><body><span id='math' style='position: absolute; color:black;'>\\[%s\\]</span></body></html>";

}

