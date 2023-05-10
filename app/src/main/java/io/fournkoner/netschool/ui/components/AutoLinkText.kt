package io.fournkoner.netschool.ui.components

import android.content.Intent
import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.net.toUri

@Composable
fun AutoLinkText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    linkStyle: TextStyle
) {
    val clickableText = remember(text) { text.linkify(linkStyle.toSpanStyle()) }
    val context = LocalContext.current

    ClickableText(
        text = clickableText,
        style = textStyle,
        modifier = modifier,
        onClick = { position ->
            clickableText.urlAt(position) {
                context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri()))
            }
        }
    )
}

private fun String.linkify(linkStyle: SpanStyle) = buildAnnotatedString {
    append(this@linkify)

    val spannable = SpannableString(this@linkify)
    Linkify.addLinks(spannable, Linkify.WEB_URLS)

    val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)
    for (span in spans) {
        val start = spannable.getSpanStart(span)
        val end = spannable.getSpanEnd(span)

        addStyle(
            start = start,
            end = end,
            style = linkStyle
        )
        addStringAnnotation(
            tag = "URL",
            annotation = span.url,
            start = start,
            end = end
        )
    }
}

private fun AnnotatedString.urlAt(position: Int, onFound: (String) -> Unit) =
    getStringAnnotations("URL", position, position).firstOrNull()?.item?.let {
        onFound(it)
    }
