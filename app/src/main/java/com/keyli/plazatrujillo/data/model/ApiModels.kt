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
    @SerializedName("quantity") val quantity: Int? = null
)

data class UpsertStockRequest(
    @SerializedName("items") val items: List<StockItem>
)

data class UpsertStockResponse(
    @SerializedName("updated") val updated: List<StockItem>? = null
)

data class SendLaundryRequest(
    @SerializedName("room_number") val roomNumber: String,
    @SerializedName("items") val items: List<LaundryItem>
)

data class LaundryItem(
    @SerializedName("category") val category: String,
    @SerializedName("quantity") val quantity: Int
)

data class SendLaundryResponse(
    @SerializedName("order") val order: LaundryOrder? = null
)

data class LaundryOrder(
    @SerializedName("code") val code: String? = null,
    @SerializedName("room_number") val roomNumber: String? = null,
    @SerializedName("items") val items: List<LaundryItem>? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class ReturnOrderResponse(
    @SerializedName("order") val order: LaundryOrder? = null
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
    @SerializedName("damage") val damage: Map<String, Int>? = null
)

// ==================== CAJA MODELS ====================
data class ListTodayTransactionsResponse(
    @SerializedName("transactions") val transactions: List<Transaction>? = null
)

data class Transaction(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("method") val method: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("description") val description: String? = null
)

data class TodayTotalsResponse(
    @SerializedName("totals") val totals: Totals? = null
)

data class Totals(
    @SerializedName("methods") val methods: Map<String, Double>? = null,
    @SerializedName("total") val total: Double? = null
)

data class CreatePaymentRequest(
    @SerializedName("amount") val amount: Double,
    @SerializedName("method") val method: String,
    @SerializedName("description") val description: String? = null
)

data class CreatePaymentResponse(
    @SerializedName("transaction") val transaction: Transaction? = null
)

data class EmitReceiptRequest(
    @SerializedName("transaction_id") val transactionId: Int,
    @SerializedName("client_name") val clientName: String? = null
)

data class EmitReceiptResponse(
    @SerializedName("receipt") val receipt: Receipt? = null
)

data class Receipt(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("transaction_id") val transactionId: Int? = null,
    @SerializedName("client_name") val clientName: String? = null,
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("date") val date: String? = null
)

data class TodayClientsResponse(
    @SerializedName("clients") val clients: List<Client>? = null,
    @SerializedName("total") val total: Int? = null
)

data class AllClientsResponse(
    @SerializedName("clients") val clients: List<Client>? = null,
    @SerializedName("total") val total: Int? = null
)

data class Client(
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null
)

data class PaidClientsResponse(
    @SerializedName("clients") val clients: List<Client>? = null,
    @SerializedName("total") val total: Int? = null
)

data class PaidClientsDetailsResponse(
    @SerializedName("clients") val clients: List<ClientDetail>? = null,
    @SerializedName("total") val total: Int? = null
)

data class ClientDetail(
    @SerializedName("name") val name: String? = null,
    @SerializedName("dni") val dni: String? = null,
    @SerializedName("address") val address: String? = null
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
data class SystemStatusResponse(
    @SerializedName("status") val status: SystemStatus? = null
)

data class SystemStatus(
    @SerializedName("water_heating") val waterHeating: Boolean? = null,
    @SerializedName("temperature") val temperature: Double? = null,
    @SerializedName("last_update") val lastUpdate: String? = null
)

data class UpdateSystemStatusRequest(
    @SerializedName("water_heating") val waterHeating: Boolean? = null,
    @SerializedName("temperature") val temperature: Double? = null
)

data class UpdateSystemStatusResponse(
    @SerializedName("status") val status: SystemStatus? = null
)

data class BriquetteHistoryResponse(
    @SerializedName("history") val history: List<BriquetteRecord>? = null
)

data class BriquetteRecord(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("notes") val notes: String? = null
)

data class RegisterBriquetteChangeRequest(
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("notes") val notes: String? = null
)

data class RegisterBriquetteChangeResponse(
    @SerializedName("record") val record: BriquetteRecord? = null
)

data class MaintenanceIssuesResponse(
    @SerializedName("issues") val issues: List<MaintenanceIssue>? = null
)

data class MaintenanceIssue(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("room_number") val roomNumber: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("reported_at") val reportedAt: String? = null
)

data class ReportIssueRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("room_number") val roomNumber: String? = null
)

data class ReportIssueResponse(
    @SerializedName("issue") val issue: MaintenanceIssue? = null
)

data class DeleteIssueResponse(
    @SerializedName("message") val message: String? = null
)

data class BlockedRoomsResponse(
    @SerializedName("rooms") val rooms: List<BlockedRoom>? = null
)

data class BlockedRoom(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("room_number") val roomNumber: String? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("blocked_until") val blockedUntil: String? = null
)

data class BlockRoomRequest(
    @SerializedName("room_number") val roomNumber: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("blocked_until") val blockedUntil: String? = null
)

data class BlockRoomResponse(
    @SerializedName("room") val room: BlockedRoom? = null
)

data class UnblockRoomResponse(
    @SerializedName("message") val message: String? = null
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

