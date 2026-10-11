const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read folders or creations", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("folders").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("creations").get());
});

test("Cross-user access: Bob cannot read Alice's folders or creations", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("folders").doc("f1").set({
      userId: ALICE_UID,
      name: "Shonen Skecthes",
      color: "#FF2E63",
      icon: "book",
      itemCount: 1,
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("folders").doc("f1").get());
});

test("Authenticated user: Alice can create and read her own folders and creations", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("folders").doc("f1").set({
      userId: ALICE_UID,
      name: "Bocetos Shonen",
      color: "#FF2E63",
      icon: "book",
      itemCount: 0,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("creations").doc("c1").set({
      userId: ALICE_UID,
      title: "Guerrero Ninja",
      prompt: "Un guerrero ninja bajo la lluvia",
      genre: "shonen",
      style: "modern_digital",
      mediaType: "image",
      aspectRatio: "16:9",
      tags: ["ninja", "lluvia"],
      isFavorite: false,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("folders").get());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("creations").get());
});

test("Schema validation: rejects invalid anime genre", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("creations").doc("c2").set({
      userId: ALICE_UID,
      title: "Test",
      prompt: "A character",
      genre: "invalid_genre_not_anime",
      style: "modern_digital",
      mediaType: "image",
      aspectRatio: "1:1",
      tags: [],
      isFavorite: false,
      createdAt: now,
      updatedAt: now,
    })
  );
});
