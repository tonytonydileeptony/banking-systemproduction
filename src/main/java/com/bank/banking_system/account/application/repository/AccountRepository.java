package com.bank.banking_system.account.application.repository;


import com.bank.banking_system.account.application.model.AccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    /*❌ Double withdrawal
      ❌ Race condition
      ❌ Negative balance bug
      ❌ Double Withdrawal
Problem

User clicks Transfer button twice (or network retry happens).

Without protection
Balance = 1000

Request 1 → Debit 500 → Balance = 500
Request 2 → Debit 500 → Balance = 0   ❌ (User intended only once)


Money deducted twice.

Why it happens

Same API called multiple times

No idempotency check

No locking

Real Banking Fix

Idempotency key (transactionId)

DB lock

Unique constraint

We will implement Day 7 — Idempotency.

2 ❌ Race Condition
Problem

Two requests hit at same time.

Balance = 1000
Thread A reads → 1000
Thread B reads → 1000

Thread A debit 800 → new = 200
Thread B debit 800 → new = 200 ❌ (Should fail)


Final balance = 200 instead of -600 or reject → Data corruption.

Why it happens

Multiple threads read same old value before update.

Fix (What we added today)
@Lock(LockModeType.PESSIMISTIC_WRITE)


This makes:

Thread A locks row → Thread B must WAIT


So correct flow:

Thread A debit → Balance = 200
Thread B reads → 200 → Reject (Insufficient balance)


No corruption ✅

3 ❌ Negative Balance Bug
Problem

Account allows debit more than balance.

Example:

Balance = 1000
Debit = 1500 → Balance = -500 ❌


Money created from nothing 💥 (Very dangerous)

Fix — Domain Rule inside Entity

Your AccountEntity must have:

public void debit(BigDecimal amount) {
    if (this.balance.compareTo(amount) < 0) {
        throw new RuntimeException("Insufficient balance");
    }
    this.balance = this.balance.subtract(amount);
}


Now:

Balance = 1000
Debit = 1500 → ❌ Exception → No update


Safe banking rule ✅

How All 3 Are Connected
Problem	Cause	Fix
Double Withdrawal	Same request twice	Idempotency
Race Condition	Parallel threads	DB Lock
Negative Balance	No validation	Domain rule

Together they ensure:

✔ No money duplication
✔ No data corruption
✔ Strong consistency (ACID)
✔ Real banking safety

Real Bank Production Flow

When transfer happens:

Lock sender account 🔒

Check balance ✔

Debit ✔

Credit ✔

Commit transaction ✔

Prevent duplicate transfer ✔

Interview Golden Answer ⭐

Q: How do you prevent race condition in banking transfer?

Answer:

Use database row-level locking (Pessimistic/Optimistic), transactional boundary, and domain validation to ensure atomic and consistent balance updates.
Pessimistic vs Optimistic Locking

Both are used to prevent race conditions & data corruption when multiple users update same row (ex: account balance).

1️⃣ Pessimistic Locking (Lock First, Then Work)
Idea

“I don’t trust others — lock the row before updating.”

When one transaction reads a row → DB locks it → others must WAIT.

Flow (Bank Example)

Balance = 1000

Thread A → locks account row 🔒
Thread B → tries → WAIT ⏳

Thread A debit 800 → Balance = 200 → Commit → Unlock

Thread B reads new balance = 200 → Reject (Insufficient funds)


✔ No race condition
✔ Strong consistency
❌ Slower (threads wait)
❌ Can cause deadlock if not careful

Spring Boot / JPA Code
Repository
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT a FROM AccountEntity a WHERE a.id = :id")
Optional<AccountEntity> findByIdForUpdate(Long id);

Service
@Transactional
public void transfer(Long fromId, Long toId, BigDecimal amount) {
    AccountEntity from = repo.findByIdForUpdate(fromId).orElseThrow();
    AccountEntity to   = repo.findByIdForUpdate(toId).orElseThrow();

    from.debit(amount);
    to.credit(amount);

    repo.save(from);
    repo.save(to);
}

When to Use (Banking Rule)

Use Pessimistic Locking when:

Money transfer 💰

Wallet balance

Stock deduction

Critical financial data

High conflict expected

👉 Your banking project → Use PESSIMISTIC for transfer

2️⃣ Optimistic Locking (Trust First, Check Later)
Idea

“I assume no conflict — but I verify before commit.”

Instead of locking, we use version number.

If another transaction changed row → update FAIL → retry.

Flow

Balance = 1000, version = 1

Thread A reads → version 1
Thread B reads → version 1

Thread A debit → version becomes 2 → Commit ✔

Thread B tries update WHERE version=1 → ❌ FAIL


No corruption — one fails safely.

JPA Code
Entity
@Version
private Long version;

No special query needed
@Transactional
public void debit(Long id, BigDecimal amount) {
    AccountEntity acc = repo.findById(id).orElseThrow();
    acc.debit(amount);
    repo.save(acc);  // Fails automatically if version mismatch
}


Spring throws:

OptimisticLockException


You can retry.

When to Use

Use Optimistic Locking when:

Read-heavy systems

Low conflict probability

Profile update

Product catalog

User data

❌ Not ideal for high-frequency balance updates

Key Differences
Feature	Pessimistic	Optimistic
Locking	DB row locked	No lock
Performance	Slower	Faster
Conflict handling	Others WAIT	One FAILS
Deadlock risk	Yes	No
Best for	Banking, money	Read-heavy apps
Implementation	@Lock	@Version
Real Banking Choice

Most banks use:

Pessimistic locking for balance updates

Optimistic for non-financial data

Plus Idempotency + Transactions

What Happens If You Use NONE ❌

You get:

Race condition

Double withdrawal

Negative balance

Money corruption 💥

Interview Questions & Answers
Q1: Difference between pessimistic & optimistic locking?

Answer:
Pessimistic locking locks DB row before update preventing concurrent access, while optimistic locking uses versioning and fails update if data changed by another transaction.

Q2: Which locking for banking transfer?

Answer:
Pessimistic locking — ensures strong consistency and prevents concurrent balance modification.

Q3: Does optimistic locking prevent race condition?

Yes — by detecting conflict using version check.

In Your Project (Important)

Use:

Pessimistic locking → transfer

Optimistic locking → user/profile updates

Later we will also add:

Retry mechanism

Idempotency

Distributed lock (Redis)

Kafka event safety
    */


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AccountEntity> findById(Long id);
}

