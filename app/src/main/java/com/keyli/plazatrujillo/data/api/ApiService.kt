package com.keyli.plazatrujillo.data.api

import com.keyli.plazatrujillo.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface principal que contiene todos los endpoints de la API
 */
interface ApiService {
    
    // ==================== DASHBOARD ====================
    @GET("/api/dashboard/metrics/")
    suspend fun getDashboardMetrics(): Response<DashboardMetricsResponse>
    
    @GET("/api/dashboard/monthly-revenue/")
    suspend fun getMonthlyRevenue(): Response<MonthlyRevenueResponse>
    
    @GET("/api/dashboard/payment-methods/")
    suspend fun getPaymentMethods(): Response<PaymentMethodsResponse>
    
    @GET("/api/dashboard/occupancy-weekly/")
    suspend fun getOccupancyWeekly(): Response<OccupancyWeeklyResponse>
    
    @GET("/api/dashboard/today-checkins-checkouts/")
    suspend fun getTodayCheckinsCheckouts(): Response<TodayCheckinsCheckoutsResponse>
    
    @GET("/api/dashboard/recent-reservations/")
    suspend fun getRecentReservations(): Response<RecentReservationsResponse>
    
    @GET("/api/dashboard/statistics/")
    suspend fun getStatistics(): Response<StatisticsResponse>
    
    // ==================== AUTH / USERS ====================
    @GET("/api/auth/admin/users/")
    suspend fun listUsers(): Response<ListUsersResponse>
    
    @POST("/api/auth/admin/users/create/")
    suspend fun createUser(@Body request: CreateUserRequest): Response<CreateUserResponse>
    
    @PUT("/api/auth/admin/users/{uid}/role/")
    suspend fun updateUser(
        @Path("uid") uid: String,
        @Body request: UpdateUserRequest
    ): Response<UpdateUserResponse>
    
    @DELETE("/api/auth/admin/users/{uid}/")
    suspend fun deleteUser(@Path("uid") uid: String): Response<DeleteUserResponse>
    
    @PATCH("/api/auth/admin/users/{uid}/toggle-status/")
    suspend fun toggleUserStatus(@Path("uid") uid: String): Response<ToggleUserStatusResponse>
    
    @GET("/api/auth/profile/")
    suspend fun getOwnProfile(): Response<ProfileResponse>
    
    @PATCH("/api/auth/profile/update/")
    suspend fun updateOwnProfile(@Body request: UpdateProfileRequest): Response<UpdateProfileResponse>
    
    // ==================== RESERVATIONS ====================
    @GET("/api/reservations/")
    suspend fun listReservations(): Response<ListReservationsResponse>
    
    @GET("/api/reservations/calendar/")
    suspend fun getCalendarEvents(): Response<CalendarEventsResponse>
    
    @GET("/api/reservations/calendar/notes/")
    suspend fun getCalendarNotes(): Response<CalendarNotesResponse>
    
    @PUT("/api/reservations/calendar/notes/{date}/")
    suspend fun setCalendarNote(
        @Path("date") date: String,
        @Body request: SetCalendarNoteRequest
    ): Response<SetCalendarNoteResponse>
    
    @DELETE("/api/reservations/calendar/notes/{date}/")
    suspend fun deleteCalendarNote(@Path("date") date: String): Response<Unit>
    
    @POST("/api/reservations/create/")
    suspend fun createReservation(@Body request: CreateReservationRequest): Response<CreateReservationResponse>
    
    @PATCH("/api/reservations/{reservationId}/")
    suspend fun updateReservation(
        @Path("reservationId") reservationId: String,
        @Body request: UpdateReservationRequest
    ): Response<UpdateReservationResponse>
    
    @DELETE("/api/reservations/{reservationId}/")
    suspend fun deleteReservation(@Path("reservationId") reservationId: String): Response<Unit>
    
    @GET("/api/reservations/lookup/")
    suspend fun lookupDocument(
        @Query("type") type: String,
        @Query("number") number: String
    ): Response<LookupDocumentResponse>
    
    @GET("/api/reservations/rooms/available/")
    suspend fun getAvailableRooms(
        @Query("check_in") checkIn: String,
        @Query("check_out") checkOut: String,
        @Query("exclude_reservation") excludeReservation: String? = null
    ): Response<AvailableRoomsResponse>
    
    @GET("/api/reservations/rooms/all/")
    suspend fun getAllRooms(): Response<AllRoomsResponse>
    
    @GET("/api/reservations/{reservationId}/")
    suspend fun getReservationDetail(@Path("reservationId") reservationId: String): Response<ReservationDetailResponse>
    
    // ==================== LAVANDERIA ====================
    @GET("/api/lavanderia/stock/")
    suspend fun getStock(): Response<StockResponse>
    
    @POST("/api/lavanderia/stock/upsert/")
    suspend fun upsertStock(@Body request: UpsertStockRequest): Response<UpsertStockResponse>
    
    @POST("/api/lavanderia/send/")
    suspend fun sendLaundry(@Body request: SendLaundryRequest): Response<SendLaundryResponse>
    
    @POST("/api/lavanderia/return/{orderCode}/")
    suspend fun returnOrder(@Path("orderCode") orderCode: String): Response<ReturnOrderResponse>
    
    @GET("/api/lavanderia/orders/")
    suspend fun listOrders(): Response<ListOrdersResponse>
    
    @POST("/api/lavanderia/damage/")
    suspend fun updateDamage(@Body request: UpdateDamageRequest): Response<UpdateDamageResponse>
    
    // ==================== CAJA ====================
    @GET("/api/cajacobros/transactions/today/")
    suspend fun listTodayTransactions(@Query("date") date: String? = null): Response<ListTodayTransactionsResponse>
    
    @GET("/api/cajacobros/totals/today/")
    suspend fun todayTotals(@Query("date") date: String? = null): Response<TodayTotalsResponse>
    
    @POST("/api/cajacobros/payments/create/")
    suspend fun createPayment(@Body request: CreatePaymentRequest): Response<CreatePaymentResponse>
    
    @POST("/api/cajacobros/receipt/emit/")
    suspend fun emitReceipt(@Body request: EmitReceiptRequest): Response<EmitReceiptResponse>
    
    @GET("/api/cajacobros/clients/today/")
    suspend fun todayClients(): Response<TodayClientsResponse>
    
    @GET("/api/cajacobros/clients/")
    suspend fun allClients(): Response<AllClientsResponse>
    
    @GET("/api/reservations/clients/paid/")
    suspend fun paidClients(): Response<PaidClientsResponse>
    
    @GET("/api/reservations/clients/paid/details/")
    suspend fun paidClientsDetails(): Response<PaidClientsDetailsResponse>
    
    // ==================== MESSAGING ====================
    @GET("/api/messaging/conversations/")
    suspend fun listConversations(): Response<ListConversationsResponse>
    
    @GET("/api/messaging/users/")
    suspend fun listUsersForMessaging(): Response<ListUsersForMessagingResponse>
    
    @GET("/api/messaging/messages/{otherUserUid}/")
    suspend fun getMessages(@Path("otherUserUid") otherUserUid: String): Response<GetMessagesResponse>
    
    @POST("/api/messaging/send/{otherUserUid}/")
    suspend fun sendMessage(
        @Path("otherUserUid") otherUserUid: String,
        @Body request: SendMessageRequest
    ): Response<SendMessageResponse>
    
    // ==================== MANTENIMIENTO ====================
    @GET("/api/mantenimiento/system/status/")
    suspend fun getSystemStatus(): Response<SystemStatusResponse>
    
    @POST("/api/mantenimiento/system/update/")
    suspend fun updateSystemStatus(@Body request: UpdateSystemStatusRequest): Response<UpdateSystemStatusResponse>
    
    @GET("/api/mantenimiento/briquettes/history/")
    suspend fun getBriquetteHistory(): Response<BriquetteHistoryResponse>
    
    @POST("/api/mantenimiento/briquettes/register/")
    suspend fun registerBriquetteChange(@Body request: RegisterBriquetteChangeRequest): Response<RegisterBriquetteChangeResponse>
    
    @GET("/api/mantenimiento/issues/")
    suspend fun getMaintenanceIssues(): Response<MaintenanceIssuesResponse>
    
    @POST("/api/mantenimiento/issues/report/")
    suspend fun reportIssue(@Body request: ReportIssueRequest): Response<ReportIssueResponse>
    
    @DELETE("/api/mantenimiento/issues/delete/{issueId}/")
    suspend fun deleteIssue(@Path("issueId") issueId: Int): Response<DeleteIssueResponse>
    
    @GET("/api/mantenimiento/rooms/blocked/")
    suspend fun getBlockedRooms(): Response<BlockedRoomsResponse>
    
    @POST("/api/mantenimiento/rooms/block/")
    suspend fun blockRoom(@Body request: BlockRoomRequest): Response<BlockRoomResponse>
    
    @DELETE("/api/mantenimiento/rooms/unblock/{roomId}/")
    suspend fun unblockRoom(@Path("roomId") roomId: Int): Response<UnblockRoomResponse>
    
    // ==================== CHATBOT ====================
    @POST("/api/chatbot/message/")
    suspend fun processChatbotMessage(@Body request: ProcessChatbotMessageRequest): Response<ProcessChatbotMessageResponse>
    
    @GET("/api/chatbot/history/")
    suspend fun getChatbotHistory(@Query("session_id") sessionId: String? = null): Response<ChatbotHistoryResponse>
    
    @POST("/api/chatbot/end-session/")
    suspend fun endChatbotSession(@Body request: EndChatbotSessionRequest): Response<EndChatbotSessionResponse>
}

