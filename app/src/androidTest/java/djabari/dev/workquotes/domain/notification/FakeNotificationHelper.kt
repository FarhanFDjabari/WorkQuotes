package djabari.dev.workquotes.domain.notification

import djabari.dev.workquotes.data.model.Quote

class FakeNotificationHelper : NotificationHelper {
    override fun showQuoteNotification(quote: Quote) {
        // No-op
    }
}
