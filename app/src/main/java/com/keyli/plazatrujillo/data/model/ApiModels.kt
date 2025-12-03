package com.keyli.plazatrujillo.data.model

import com.google.gson.annotations.SerializedName

// ==================== DASHBOARD MODELS ====================
// Dashboard metrics devuelve directamente el objeto, no envuelto
data class DashboardMetricsResponse(
    @SerializedName("monthly_revenue") val monthlyRevenue: RevenueMetric? = null,
    @SerializedName("total_revenue") val totalRevenue: RevenueMetric? = null,
    @SerializedName("occupancy_rate") val occupancyRate: OccupancyMetric? = null,
    @SerializedName("adr") val adr: AdrMetric? = null
)

data class RevenueMetric(
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("change_percent") val changePercent: Double? = null
)

data class OccupancyMetric(
    @SerializedName("rate") val rate: Double? = null,
    @SerializedName("change_percent") val changePercent: Double? = null
)

data class AdrMetric(
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("change_percent") val changePercent: Double? = null
)

// Monthly revenue devuelve {"data": [123, 456, ...]}
data class MonthlyRevenueResponse(
    @SerializedName("data") val data: List<Double>? = null
)

// Payment methods devuelve {"data": {"Efectivo": 123, ...}}
data class PaymentMethodsResponse(
    @SerializedName("data") val data: Map<String, Double>? = null
)

// Occupancy weekly devuelve {"data": [65, 70, ...]}
data class OccupancyWeeklyResponse(
    @SerializedName("data") val data: List<Double>? = null
)

data class TodayCheckinsCheckoutsResponse(
    @SerializedName("checkins") val checkins: List<CheckinCheckoutItem>? = null,
    @SerializedName("checkouts") val checkouts: List<CheckinCheckoutItem>? = null
)

data class CheckinCheckoutItem(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("reservation_id") val reservationId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("room") val room: String? = null,
    @SerializedName("time") val time: String? = null
)

data class RecentReservationsResponse(
    @SerializedName("reservations") val reservations: List<RecentReservationItem>? = null
)

data class RecentReservationItem(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("guestName") val guestName: String? = null,
    @SerializedName("room") val room: String? = null,
    @SerializedName("checkIn") val checkIn: String? = null,
    @SerializedName("checkOut") val checkOut: String? = null,
    @SerializedName("guests") val guests: String? = null,
    @SerializedName("total") val total: String? = null,
    @SerializedName("status") val status: String? = null
)

data class StatisticsResponse(
    @SerializedName("income") val income: List<Double>? = null,
    @SerializedName("labels") val labels: List<String>? = null
)

// ==================== USER MODELS ====================
data class ListUsersResponse(
    @SerializedName("users") val users: List<User>? = null
)

data class User(
    @SerializedName("uid") val uid: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("disabled") val disabled: Boolean? = null,  // Backend usa "disabled" no "is_active"
    @SerializedName("email_verified") val emailVerified: Boolean? = null,
    @SerializedName("creation_time") val creationTime: Long? = null,
    @SerializedName("salary") val salary: String? = null,  // Backend devuelve string o vacío
    @SerializedName("entry_date") val entryDate: String? = null,  // Backend devuelve string o vacío
    @SerializedName("attendance") val attendance: String? = null,  // Backend devuelve string o vacío
    @SerializedName("profile_photo_url") val profilePhotoUrl: String? = null
)

data class CreateUserRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("salary") val salary: String? = null,  // Backend espera string o vacío
    @SerializedName("entry_date") val entryDate: String? = null,  // Backend espera string o vacío
    @SerializedName("attendance") val attendance: String? = null  // Backend espera string o vacío
)

data class CreateUserResponse(
    @SerializedName("user") val user: User? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("email_sent") val emailSent: Boolean? = null
)

data class UpdateUserRequest(
    @SerializedName("role") val role: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("salary") val salary: String? = null,  // Backend espera string o vacío
    @SerializedName("entry_date") val entryDate: String? = null,  // Backend espera string o vacío
    @SerializedName("attendance") val attendance: String? = null  // Backend espera string o vacío
)

data class UpdateUserResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user_id") val userId: String? = null
)

data class DeleteUserResponse(
    @SerializedName("message") val message: String? = null
)

data class ToggleUserStatusResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("disabled") val disabled: Boolean? = null
)

data class ProfileResponse(
    @SerializedName("profile") val profile: UserProfile? = null
)

data class UserProfile(
    @SerializedName("uid") val uid: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("profile_photo_url") val profilePhotoUrl: String? = null,
    @SerializedName("email_verified") val emailVerified: Boolean? = null
)

data class UpdateProfileRequest(
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("profile_photo_url") val profilePhotoUrl: String? = null
)

data class UpdateProfileResponse(
    @SerializedName("profile") val profile: UserProfile? = null
)

// ==================== RESERVATION MODELS ====================
data class ListReservationsResponse(
    @SerializedName("reservations") val reservations: List<Reservation>? = null
)

data class Reservation(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("reservationId") val reservationId: String? = null,
    @SerializedName("channel") val channel: String? = null,
    @SerializedName("guest") val guest: String? = null,
    @SerializedName("room") val room: String? = null,
    @SerializedName("rooms") val rooms: List<String>? = null,
    @SerializedName("checkIn") val checkIn: String? = null,
    @SerializedName("checkOut") val checkOut: String? = null,
    @SerializedName("total") val total: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("paid") val paid: Boolean? = null,
    @SerializedName("documentType") val documentType: String? = null,
    @SerializedName("documentNumber") val documentNumber: String? = null,
    @SerializedName("arrivalTime") val arrivalTime: String? = null,
    @SerializedName("departureTime") val departureTime: String? = null,
    @SerializedName("numPeople") val numPeople: Int? = null,
    @SerializedName("numAdults") val numAdults: Int? = null,
    @SerializedName("numChildren") val numChildren: Int? = null,
    @SerializedName("numRooms") val numRooms: Int? = null,
    @SerializedName("companions") val companions: List<CompanionItem>? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("department") val department: String? = null,
    @SerializedName("province") val province: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("roomType") val roomType: String? = null,
    @SerializedName("taxpayerType") val taxpayerType: String? = null,
    @SerializedName("businessStatus") val businessStatus: String? = null,
    @SerializedName("businessCondition") val businessCondition: String? = null
)

data class CompanionItem(
    @SerializedName("name") val name: String? = null,
    @SerializedName("documentType") val documentType: String? = null,
    @SerializedName("documentNumber") val documentNumber: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("department") val department: String? = null,
    @SerializedName("province") val province: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("taxpayerType") val taxpayerType: String? = null,
    @SerializedName("businessStatus") val businessStatus: String? = null,
    @SerializedName("businessCondition") val businessCondition: String? = null
)

data class CalendarEventsResponse(
    @SerializedName("events") val events: List<CalendarEvent>? = null
)

data class CalendarEvent(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("start") val start: String? = null,
    @SerializedName("end") val end: String? = null,
    @SerializedName("extendedProps") val extendedProps: CalendarEventProps? = null
)

data class CalendarEventProps(
    @SerializedName("calendar") val calendar: String? = null
)

data class CalendarNotesResponse(
    @SerializedName("notes") val notes: List<CalendarNote>? = null
)

data class CalendarNote(
    @SerializedName("date") val date: String? = null,
    @SerializedName("text") val text: String? = null
)

data class SetCalendarNoteRequest(
    @SerializedName("text") val text: String
)

data class SetCalendarNoteResponse(
    @SerializedName("note") val note: CalendarNote? = null
)

data class CreateReservationRequest(
    @SerializedName("channel") val channel: String? = null,
    @SerializedName("guest") val guest: String,
    @SerializedName("room") val room: String? = null,
    @SerializedName("rooms") val rooms: List<String>? = null,
    @SerializedName("checkIn") val checkIn: String,
    @SerializedName("checkOut") val checkOut: String,
    @SerializedName("total") val total: Double? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("paid") val paid: Boolean? = null,
    @SerializedName("documentType") val documentType: String? = null,
    @SerializedName("documentNumber") val documentNumber: String? = null,
    @SerializedName("arrivalTime") val arrivalTime: String? = null,
    @SerializedName("departureTime") val departureTime: String? = null,
    @SerializedName("numPeople") val numPeople: Int? = null,
    @SerializedName("numAdults") val numAdults: Int? = null,
    @SerializedName("numChildren") val numChildren: Int? = null,
    @SerializedName("numRooms") val numRooms: Int? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("department") val department: String? = null,
    @SerializedName("province") val province: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("roomType") val roomType: String? = null,
    @SerializedName("taxpayerType") val taxpayerType: String? = null,
    @SerializedName("businessStatus") val businessStatus: String? = null,
    @SerializedName("businessCondition") val businessCondition: String? = null,
    @SerializedName("companions") val companions: List<CompanionItem>? = null
)

data class CreateReservationResponse(
    @SerializedName("reservation") val reservation: Reservation? = null
)

data class UpdateReservationRequest(
    @SerializedName("guest") val guest: String? = null,
    @SerializedName("room") val room: String? = null,
    @SerializedName("checkIn") val checkIn: String? = null,
    @SerializedName("checkOut") val checkOut: String? = null,
    @SerializedName("total") val total: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("paid") val paid: Boolean? = null,
    @SerializedName("arrivalTime") val arrivalTime: String? = null,
    @SerializedName("departureTime") val departureTime: String? = null,
    @SerializedName("roomType") val roomType: String? = null
)

data class UpdateReservationResponse(
    @SerializedName("reservation") val reservation: Reservation? = null
)

data class LookupDocumentResponse(
    @SerializedName("name") val name: String? = null,
    @SerializedName("raw") val raw: LookupRawData? = null
)

data class LookupRawData(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("data") val data: Map<String, Any?>? = null
)

data class AvailableRoomsResponse(
    @SerializedName("rooms") val rooms: List<Room>? = null
)

data class AllRoomsResponse(
    @SerializedName("rooms") val rooms: List<Room>? = null
)

data class Room(
    @SerializedName("code") val code: String? = null,
    @SerializedName("floor") val floor: Int? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("status") val status: String? = null
)

data class ReservationDetailResponse(
    @SerializedName("reservation") val reservation: Reservation? = null
)

// Para usar con el ID numérico en las URLs
data class ReservationDetailByIdResponse(
    @SerializedName("reservation") val reservation: Reservation? = null
)

// ==================== LAVANDERIA MODELS ====================
data class StockResponse(
    @SerializedName("stock") val stock: List<StockItem>? = null
)

data class StockItem(
    @SerializedName("category") val category: String? = null,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("disponible") val disponible: Int? = null,
    @SerializedName("lavanderia") val lavanderia: Int? = null,
    @SerializedName("danado") val danado: Int? = null
)

data class StockItemRequest(
    @SerializedName("category") val category: String,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("disponible") val disponible: Int? = null,
    @SerializedName("lavanderia") val lavanderia: Int? = null,
    @SerializedName("danado") val danado: Int? = null
)

data class UpsertStockRequest(
    @SerializedName("items") val items: List<StockItemRequest>
)

data class UpsertStockResponse(
    @SerializedName("updated") val updated: List<StockItem>? = null
)

data class SendLaundryRequest(
    @SerializedName("toalla_grande") val toallaGrande: Int = 0,
    @SerializedName("toalla_mediana") val toallaMediana: Int = 0,
    @SerializedName("toalla_chica") val toallaChica: Int = 0,
    @SerializedName("sabana_media_plaza") val sabanaMediaPlaza: Int = 0,
    @SerializedName("sabana_una_plaza") val sabanaUnaPlaza: Int = 0,
    @SerializedName("cubrecama_media_plaza") val cubrecamaMediaPlaza: Int = 0,
    @SerializedName("cubrecama_una_plaza") val cubrecamaUnaPlaza: Int = 0,
    @SerializedName("funda") val funda: Int = 0
)

data class SendLaundryResponse(
    @SerializedName("order") val order: LaundryOrder? = null
)

data class LaundryOrder(
    @SerializedName("order_code") val orderCode: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("returned_at") val returnedAt: String? = null,
    @SerializedName("toalla_grande") val toallaGrande: Int? = null,
    @SerializedName("toalla_mediana") val toallaMediana: Int? = null,
    @SerializedName("toalla_chica") val toallaChica: Int? = null,
    @SerializedName("sabana_media_plaza") val sabanaMediaPlaza: Int? = null,
    @SerializedName("sabana_una_plaza") val sabanaUnaPlaza: Int? = null,
    @SerializedName("cubrecama_media_plaza") val cubrecamaMediaPlaza: Int? = null,
    @SerializedName("cubrecama_una_plaza") val cubrecamaUnaPlaza: Int? = null,
    @SerializedName("funda") val funda: Int? = null
)

data class ReturnOrderResponse(
    @SerializedName("ok") val ok: Boolean? = null,
    @SerializedName("estado") val estado: String? = null,
    @SerializedName("fechaRetorno") val fechaRetorno: String? = null
)

data class ListOrdersResponse(
    @SerializedName("orders") val orders: List<LaundryOrder>? = null
)

data class UpdateDamageRequest(
    @SerializedName("category") val category: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("action") val action: String = "add"
)

data class UpdateDamageResponse(
    @SerializedName("category") val category: String? = null,
    @SerializedName("disponible") val disponible: Int? = null,
    @SerializedName("lavanderia") val lavanderia: Int? = null,
    @SerializedName("danado") val danado: Int? = null
)

// ==================== CAJA MODELS ====================
data class ListTodayTransactionsResponse(
    @SerializedName("transactions") val transactions: List<CajaTransaction>? = null
)

data class CajaTransaction(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("transactionId") val transactionId: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("guest") val guest: String? = null,
    @SerializedName("method") val method: String? = null,
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("status") val status: String? = null
)

data class TodayTotalsResponse(
    @SerializedName("totals") val totals: CajaTotals? = null
)

data class CajaTotals(
    @SerializedName("methods") val methods: MethodTotals? = null,
    @SerializedName("total") val total: Double? = null
)

data class MethodTotals(
    @SerializedName("Yape") val yape: Double? = null,
    @SerializedName("Efectivo") val efectivo: Double? = null,
    @SerializedName("Tarjeta") val tarjeta: Double? = null,
    @SerializedName("Transferencia") val transferencia: Double? = null
)

data class CreatePaymentRequest(
    @SerializedName("type") val type: String,
    @SerializedName("guest") val guest: String,
    @SerializedName("method") val method: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("reservationCode") val reservationCode: String? = null
)

// createPayment devuelve directamente el objeto, no un wrapper
data class CreatePaymentResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("transactionId") val transactionId: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("guest") val guest: String? = null,
    @SerializedName("method") val method: String? = null,
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("status") val status: String? = null
)

data class EmitReceiptRequest(
    @SerializedName("paymentId") val paymentId: Int,
    @SerializedName("numero") val numero: String? = null,
    @SerializedName("fecha") val fecha: String? = null,
    @SerializedName("senores") val senores: String? = null,
    @SerializedName("direccion") val direccion: String? = null,
    @SerializedName("dni") val dni: String? = null,
    @SerializedName("concepto") val concepto: String? = null,
    @SerializedName("importe") val importe: Double? = null,
    @SerializedName("total") val total: Double? = null,
    @SerializedName("son") val son: String? = null,
    @SerializedName("canceladoFecha") val canceladoFecha: String? = null
)

data class EmitReceiptResponse(
    @SerializedName("receipt") val receipt: CajaReceipt? = null
)

data class CajaReceipt(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("paymentId") val paymentId: Int? = null,
    @SerializedName("numero") val numero: String? = null,
    @SerializedName("fecha") val fecha: String? = null,
    @SerializedName("senores") val senores: String? = null,
    @SerializedName("direccion") val direccion: String? = null,
    @SerializedName("dni") val dni: String? = null,
    @SerializedName("concepto") val concepto: String? = null,
    @SerializedName("importe") val importe: Double? = null,
    @SerializedName("total") val total: Double? = null,
    @SerializedName("son") val son: String? = null,
    @SerializedName("canceladoFecha") val canceladoFecha: String? = null
)

data class TodayClientsResponse(
    @SerializedName("clients") val clients: List<CajaClient>? = null,
    @SerializedName("total") val total: Double? = null
)

data class AllClientsResponse(
    @SerializedName("clients") val clients: List<CajaClient>? = null,
    @SerializedName("total") val total: Double? = null
)

data class CajaClient(
    @SerializedName("guest") val guest: String? = null,
    @SerializedName("total") val total: Double? = null
)

data class PaidClientsResponse(
    @SerializedName("clients") val clients: List<PaidClient>? = null,
    @SerializedName("total") val total: Int? = null
)

data class PaidClient(
    @SerializedName("guest") val guest: String? = null,
    @SerializedName("reservationCode") val reservationCode: String? = null
)

data class PaidClientsDetailsResponse(
    @SerializedName("clients") val clients: List<PaidClientDetail>? = null,
    @SerializedName("total") val total: Int? = null
)

data class PaidClientDetail(
    @SerializedName("guest") val guest: String? = null,
    @SerializedName("dni") val dni: String? = null,
    @SerializedName("direccion") val direccion: String? = null
)

// ==================== MESSAGING MODELS ====================
data class ListConversationsResponse(
    @SerializedName("conversations") val conversations: List<ConversationItem>? = null
)

data class ConversationItem(
    @SerializedName("conversation_id") val conversationId: Int? = null,
    @SerializedName("other_user") val otherUser: ConversationUser? = null,
    @SerializedName("last_message") val lastMessage: LastMessageInfo? = null,
    @SerializedName("unread_count") val unreadCount: Int? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class ConversationUser(
    @SerializedName("uid") val uid: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("photo") val photo: String? = null
)

data class LastMessageInfo(
    @SerializedName("text") val text: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("sender_uid") val senderUid: String? = null
)

data class ListUsersForMessagingResponse(
    @SerializedName("users") val users: List<MessagingUser>? = null
)

data class MessagingUser(
    @SerializedName("uid") val uid: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("unread_count") val unreadCount: Int? = null,
    @SerializedName("last_message") val lastMessage: String? = null,
    @SerializedName("last_message_time") val lastMessageTime: String? = null
)

data class GetMessagesResponse(
    @SerializedName("conversation_id") val conversationId: Int? = null,
    @SerializedName("messages") val messages: List<ChatMessageResponse>? = null
)

data class ChatMessageResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("sender_uid") val senderUid: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("message_type") val messageType: String? = null,
    @SerializedName("attachment") val attachment: String? = null,
    @SerializedName("attachment_name") val attachmentName: String? = null,
    @SerializedName("attachment_size") val attachmentSize: Long? = null,
    @SerializedName("is_read") val isRead: Boolean? = null,
    @SerializedName("timestamp") val timestamp: String? = null
)

data class SendMessageRequest(
    @SerializedName("text") val text: String,
    @SerializedName("message_type") val messageType: String = "text",
    @SerializedName("attachment") val attachment: String? = null,
    @SerializedName("attachment_name") val attachmentName: String? = null,
    @SerializedName("attachment_size") val attachmentSize: Long? = null
)

data class SendMessageResponse(
    @SerializedName("message") val message: ChatMessageResponse? = null
)

// ==================== MANTENIMIENTO MODELS ====================

// Sistema de Agua Caliente
data class MaintenanceDateTime(
    @SerializedName("date") val date: String? = null,
    @SerializedName("time") val time: String? = null
)

data class SystemStatusResponse(
    @SerializedName("operationalStatus") val operationalStatus: String? = null,
    @SerializedName("briquettesThisMonth") val briquettesThisMonth: Int? = null,
    @SerializedName("lastMaintenance") val lastMaintenance: MaintenanceDateTime? = null,
    @SerializedName("nextMaintenance") val nextMaintenance: MaintenanceDateTime? = null
)

data class UpdateSystemStatusRequest(
    @SerializedName("operationalStatus") val operationalStatus: String? = null,
    @SerializedName("briquettesThisMonth") val briquettesThisMonth: Int? = null,
    @SerializedName("lastMaintenance") val lastMaintenance: MaintenanceDateTime? = null,
    @SerializedName("nextMaintenance") val nextMaintenance: MaintenanceDateTime? = null
)

data class UpdateSystemStatusResponse(
    @SerializedName("operationalStatus") val operationalStatus: String? = null,
    @SerializedName("briquettesThisMonth") val briquettesThisMonth: Int? = null,
    @SerializedName("lastMaintenance") val lastMaintenance: MaintenanceDateTime? = null,
    @SerializedName("nextMaintenance") val nextMaintenance: MaintenanceDateTime? = null
)

// Historial de Briquetas
data class BriquetteHistoryResponse(
    @SerializedName("history") val history: List<BriquetteRecord>? = null
)

data class BriquetteRecord(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("quantity") val quantity: Int? = null
)

data class RegisterBriquetteChangeRequest(
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("operationalStatus") val operationalStatus: String? = null
)

data class RegisterBriquetteChangeResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("quantity") val quantity: Int? = null
)

// Incidencias de Mantenimiento
data class MaintenanceIssuesResponse(
    @SerializedName("issues") val issues: List<MaintenanceIssue>? = null
)

data class MaintenanceIssue(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("room") val room: String? = null,
    @SerializedName("problem") val problem: String? = null,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("technician") val technician: String? = null,
    @SerializedName("reportedDate") val reportedDate: String? = null
)

data class ReportIssueRequest(
    @SerializedName("room") val room: String,
    @SerializedName("problem") val problem: String,
    @SerializedName("priority") val priority: String = "Media",
    @SerializedName("technician") val technician: String? = null
)

data class ReportIssueResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("room") val room: String? = null,
    @SerializedName("problem") val problem: String? = null,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("technician") val technician: String? = null,
    @SerializedName("reportedDate") val reportedDate: String? = null
)

data class DeleteIssueResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("room") val room: String? = null
)

// Habitaciones Bloqueadas
data class BlockedRoomsResponse(
    @SerializedName("rooms") val rooms: List<BlockedRoom>? = null
)

data class BlockedRoom(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("room") val room: String? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("blockedUntil") val blockedUntil: String? = null,
    @SerializedName("blockedBy") val blockedBy: String? = null
)

data class BlockRoomRequest(
    @SerializedName("room") val room: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("blockedUntil") val blockedUntil: String,
    @SerializedName("blockedBy") val blockedBy: String? = null
)

data class BlockRoomResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("room") val room: String? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("blockedUntil") val blockedUntil: String? = null,
    @SerializedName("blockedBy") val blockedBy: String? = null
)

data class UnblockRoomResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("room") val room: String? = null
)

// ==================== CHATBOT MODELS ====================
data class ProcessChatbotMessageRequest(
    @SerializedName("message") val message: String,
    @SerializedName("session_id") val sessionId: String? = null
)

data class ProcessChatbotMessageResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("session_id") val sessionId: String? = null,
    @SerializedName("error") val error: String? = null
)

data class ChatbotHistoryResponse(
    @SerializedName("conversations") val conversations: List<ChatbotConversation>? = null
)

data class ChatbotConversation(
    @SerializedName("session_id") val sessionId: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("messages") val messages: List<ChatbotHistoryMessage>? = null
)

data class ChatbotHistoryMessage(
    @SerializedName("role") val role: String? = null,  // "user" o "assistant"
    @SerializedName("content") val content: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null
)

data class EndChatbotSessionRequest(
    @SerializedName("session_id") val sessionId: String
)

data class EndChatbotSessionResponse(
    @SerializedName("message") val message: String? = null
)

