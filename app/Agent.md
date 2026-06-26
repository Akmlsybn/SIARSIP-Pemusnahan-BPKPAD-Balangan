# AGENTS.md — SIARSIP BPKPAD Balangan (Modul Pemusnahan Arsip)

> **Single Source of Truth** for all AI coding assistants (GitHub Copilot, Cursor AI, Gemini, Claude, ChatGPT, etc.) working on the **SIARSIP Archive Disposal Module**.
>
> **Read this document before generating, modifying, or refactoring any Kotlin, SQL, Room, Supabase, ViewModel, Repository, or Compose code.**

---

# 0. Golden Rules — Legal Archive Disposal Laws

These rules are **mandatory** and must never be violated.

## Rule 1 — Physical Destruction ≠ Database Hard Delete

**Never delete archive records from the database.**

Forbidden:

```sql
DELETE FROM archive_documents;
```

Forbidden:

```kotlin
archiveDao.delete(document)
```

Archive destruction only means the **physical paper is destroyed**.

Digitally, the archive **must remain forever** for legal audit purposes.

Instead, update:

```text
status = DISPOSED
disposed_at = current timestamp
berita_acara_id = UUID
```

Metadata must remain permanently.

---

## Rule 2 — 10-Year Retention Rule

Only archives that have passed their retention period may be proposed for destruction.

Formula:

```text
(document_year + 10) <= current_year
```

Every query that loads destruction candidates **must enforce this rule**.

Example SQL:

```sql
WHERE
status = 'AVAILABLE'
AND document_year + 10 <= EXTRACT(YEAR FROM NOW())
```

---

## Rule 3 — No Disposal Without Berita Acara

A document **cannot become DISPOSED** until it is attached to a valid:

* Berita Acara
* Nomor BA
* Tanggal Eksekusi
* Penanggung Jawab
* Witnesses

If `berita_acara_id` is NULL:

➡ Block the transaction.

---

## Rule 4 — Proposed Archives Become Frozen

Once an archive enters:

```text
PROPOSED
```

it is locked.

The archive cannot:

* be edited
* be borrowed
* be moved to another box
* be proposed again

---

## Rule 5 — Mandatory State Machine

No state transition may skip intermediate states.

Correct lifecycle:

```text
AVAILABLE
      │
      ▼
PROPOSED
      │
      ▼
VERIFIED
      │
      ▼
APPROVED
      │
      ▼
DISPOSED
```

Invalid examples:

```
AVAILABLE → DISPOSED
```

```
AVAILABLE → APPROVED
```

---

## Rule 6 — Every State Transition Must Produce Audit Logs

Whenever a document changes status, insert an immutable audit record.

Audit fields:

* actor_id
* archive_id
* previous_status
* new_status
* timestamp

---

## Rule 7 — UUID Safety

Room IDs are **NOT** Supabase UUIDs.

Never send local auto-increment IDs as foreign keys.

Remote IDs must always be valid UUID v4.

---

# 1. Module Identity

| Item                 | Value                      |
| -------------------- | -------------------------- |
| Module               | SIARSIP Pemusnahan Arsip   |
| Package              | `com.bpkpad.siarsip`       |
| Architecture         | MVVM + Clean Architecture  |
| UI                   | Jetpack Compose Material 3 |
| Local Database       | Room                       |
| Remote Database      | Supabase PostgreSQL        |
| Dependency Injection | Hilt                       |
| Async                | Kotlin Coroutines + Flow   |

---

# 2. Business Flow

The archive disposal process follows this exact lifecycle.

```text
Retention Expired
        │
        ▼
+-------------------+
|    AVAILABLE      |
+-------------------+
        │
User selects archive
        │
        ▼
+-------------------+
|    PROPOSED       |
+-------------------+
        │
Administrative verification
        │
        ▼
+-------------------+
|    VERIFIED       |
+-------------------+
        │
Approved by Head of BPKPAD
        │
        ▼
+-------------------+
|    APPROVED       |
+-------------------+
        │
Berita Acara created
        │
        ▼
+-------------------+
|    DISPOSED       |
+-------------------+
```

---

# 3. Screen Responsibilities

## DaftarArsipScreen

Purpose:

Displays archives eligible for destruction.

Requirements:

* Only AVAILABLE documents
* Retention expired
* Read-only archive metadata

---

## BuatBerkasUsulMusnahScreen

Purpose:

Create a legal proposal bundle.

Responsibilities:

* Create Proposal ID
* Attach selected archives
* Change status

```
AVAILABLE
→
PROPOSED
```

---

## DaftarUsulMusnahScreen

Purpose:

Display every destruction proposal.

Features:

* Search
* Filter
* Detail
* Status

---

## StatusTrackingScreen

Purpose:

Track proposal progress.

Example:

```
PROPOSED

↓

VERIFIED

↓

APPROVED

↓

DISPOSED
```

---

## BeritaAcaraScreen

Purpose:

Generate legal destruction record.

Required fields:

* Nomor Berita Acara
* Execution Date
* Responsible Officer
* Witness 1
* Witness 2
* Notes

Validation:

All required fields must be completed before execution.

---

## DetailBeritaAcaraScreen

Purpose:

Display generated Berita Acara.

Read only.

---

## LogRiwayatScreen

Purpose:

Immutable historical audit.

Never allow:

* Edit
* Delete
* Replace

---

# 4. Database Contracts

## Berita Acara Table

```sql
CREATE TABLE berita_acara_pemusnahan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nomor_ba VARCHAR(100) UNIQUE NOT NULL,
    tanggal_eksekusi DATE NOT NULL,
    penanggung_jawab VARCHAR(150) NOT NULL,
    saksi_1 VARCHAR(150) NOT NULL,
    saksi_2 VARCHAR(150),
    keterangan TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

## Archive Disposal Payload

```kotlin
@Serializable
data class DisposeArchivePayload(

    val status: String = "DISPOSED",

    @SerialName("berita_acara_id")
    val beritaAcaraId: String,

    @SerialName("disposed_at")
    val disposedAt: String
)
```

---

# 5. Clean Architecture Rules

## UI Layer

Compose screens:

* No database access
* No SQL
* No repository
* No business calculation

Compose only renders UI.

---

## ViewModel

Responsible for:

* UI State
* User Events
* Calling UseCases

---

## UseCase

Contains business logic.

Example:

```
GetEligibleDisposalArchivesUseCase
```

Responsibilities:

* Check retention
* Validate status
* Filter archives

---

## Repository

Responsibilities:

* Room
* Supabase
* Data synchronization

Repositories return:

```kotlin
Flow<ResultState<T>>
```

Never expose raw database objects.

---

# 6. Coding Standards

## No Hardcoded Strings

Government terminology must exist only inside:

```
res/values/strings.xml
```

Examples:

* Berkas Usul Musnah
* Berita Acara
* Penanggung Jawab
* Eksekusi Musnah

---

## State Hoisting

Compose screens receive:

```text
uiState: DisposalUiState
```

and emit:

```kotlin
onEvent(DisposalEvent)
```

---

## Coroutines

Database work must run using:

```kotlin
Dispatchers.IO
```

Never block the Main Thread.

---

## Result Wrapper

Repositories return:

```kotlin
ResultState.Loading

ResultState.Success

ResultState.Error
```

---

# 7. AI Safety Checklist

Before generating any code, verify:

* [ ] Did I accidentally generate a DELETE query?
* [ ] Did I use Soft Delete instead?
* [ ] Did I check the 10-year retention rule?
* [ ] Is the archive attached to a Berita Acara?
* [ ] Is every status transition logged?
* [ ] Am I using UUID instead of Room ID?
* [ ] Is database work running on Dispatchers.IO?
* [ ] Is business logic inside UseCase instead of Compose?
* [ ] Did I avoid hardcoded strings?
* [ ] Did I follow MVVM + Clean Architecture?

---

# 8. AI Prompt Reminder

Whenever generating code for this module, always remember:

1. Never perform hard delete on archive records.
2. Always enforce the legal state machine.
3. Disposal requires a valid Berita Acara.
4. Every status transition must be audited.
5. UI must never contain business logic.
6. Follow MVVM + Clean Architecture.
7. Preserve legal traceability of every archive permanently.

---

# End of Document

**Version:** 1.0

**Project:** SIARSIP BPKPAD Balangan

**Module:** Pemusnahan Arsip

**Status:** Production Coding Rules
