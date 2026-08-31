# Offline-First Architecture

ShopFlow is designed from the ground up as an offline-first application. This ensures a seamless, fast, and robust user experience regardless of the device's network connectivity.

## Core Philosophy: Local Source of Truth

The foundational principle of ShopFlow's offline-first architecture is that the **Room Database is the single source of truth**. 

- The UI **never** directly displays data retrieved directly from a network call.
- Instead, the UI exclusively observes the local Room database (often via Kotlin Flows or PagingData).
- Network requests act purely as synchronization mechanisms to update the local database.

## Paging and Remote Synchronization

For lists like the product catalog, ShopFlow leverages **Paging 3** alongside a **RemoteMediator**.

1. **Initial Load**: The `Pager` loads data from the Room `PagingSource`.
2. **Network Sync**: Simultaneously, the `RemoteMediator` checks if the local data is exhausted or stale. If so, it fetches new data from the API.
3. **Database Update**: The `RemoteMediator` saves the newly fetched data into Room.
4. **UI Update**: Because the UI is observing the Room database, the insertion automatically triggers an emission of new `PagingData` to the UI.

## Cached Data Behavior

- **Offline-with-cache**: If the user is offline but data exists in the Room database, the app functions normally, displaying the cached data. The UI may show an unobtrusive offline indicator.
- **Offline-without-cache**: If the user is offline and no cached data exists (e.g., first app launch), the application displays a clear "No Internet Connection" empty state, prompting the user to retry when connectivity is restored.

## Connectivity Monitoring vs. Network Errors

ShopFlow explicitly distinguishes between different types of network and connectivity states:

- **Connectivity State**: Monitored actively via `NetworkConnectivityMonitor`. This tells the app if the device physically has an active internet connection (Wi-Fi or Cellular).
- **Network/API Error**: Occurs when a network request is attempted but fails (e.g., DNS resolution failure, 500 Server Error, timeout) despite having a perceived active connection.
- **Cached Data State**: Indicates whether valid data is available in the local database.

## Retry Behavior

- **Automatic**: When connectivity is restored (detected via `NetworkConnectivityMonitor`), the app can automatically trigger a retry of failed network requests (like refreshing the product catalog).
- **Manual**: Pull-to-refresh and explicit "Retry" buttons allow the user to manually trigger synchronization attempts.
