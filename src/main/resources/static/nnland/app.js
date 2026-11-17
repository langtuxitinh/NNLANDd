(() => {
  "use strict";
  const $ = (s, r = document) => r.querySelector(s),
    $$ = (s, r = document) => Array.from(r.querySelectorAll(s));
  const nav = $(".main-nav"),
    navButton = $(".mobile-nav-toggle");
  if (navButton && nav)
    navButton.addEventListener("click", () => {
      const open = nav.classList.toggle("open");
      navButton.setAttribute("aria-expanded", String(open));
    });
  const sidebar = $(".portal-sidebar"),
    sideButton = $(".sidebar-toggle");
  if (sidebar && sideButton) {
    const backdrop = document.createElement("button");
    backdrop.className = "sidebar-backdrop";
    backdrop.setAttribute("aria-label", "Đóng menu tài khoản");
    document.body.appendChild(backdrop);
    const set = (open) => {
      sidebar.classList.toggle("open", open);
      backdrop.classList.toggle("open", open);
      sideButton.setAttribute("aria-expanded", String(open));
    };
    sideButton.addEventListener("click", () =>
      set(!sidebar.classList.contains("open")),
    );
    backdrop.addEventListener("click", () => set(false));
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") set(false);
    });
  }
  $$("[data-dismiss]").forEach((b) =>
    b.addEventListener("click", () => b.closest(".flash")?.remove()),
  );
  $$("[data-password-toggle]").forEach((b) =>
    b.addEventListener("click", () => {
      const input = $("input", b.parentElement);
      const show = input.type === "password";
      input.type = show ? "text" : "password";
      b.textContent = show ? "Ẩn" : "Hiện";
      b.setAttribute("aria-pressed", String(show));
    }),
  );
  $$("[data-city-select]").forEach((city) =>
    city.addEventListener("change", () => {
      const district = $("[data-district-select]", city.form),
        source = $("#district-options");
      if (!district || !source) return;
      district.replaceChildren(new Option("Tất cả khu vực", ""));
      $$("option", source.content)
        .filter((o) => o.dataset.city === city.value)
        .forEach((o) => district.appendChild(o.cloneNode(true)));
    }),
  );
  $$("[data-autosubmit]").forEach((e) =>
    e.addEventListener("change", () =>
      e.form.requestSubmit ? e.form.requestSubmit() : e.form.submit(),
    ),
  );
  $$("[data-lease-select]").forEach((e) =>
    e.addEventListener("change", () => {
      const target = new URL(e.dataset.leaseSelect, window.location.origin);
      target.searchParams.set("lease", e.value);
      window.location.assign(target.toString());
    }),
  );
  const dialog = $("#gallery-dialog");
  $$("[data-gallery]").forEach((b) =>
    b.addEventListener("click", () => {
      if (!dialog) return;
      $("#gallery-image").src = b.dataset.gallery;
      if (dialog.showModal) dialog.showModal();
      else window.open(b.dataset.gallery, "_blank", "noopener");
    }),
  );
  $$("[data-close-dialog]").forEach((b) =>
    b.addEventListener("click", () => b.closest("dialog").close()),
  );
  if (dialog)
    dialog.addEventListener("click", (e) => {
      if (e.target === dialog) dialog.close();
    });
  $$("[data-copy]").forEach((b) =>
    b.addEventListener("click", async () => {
      const value = b.dataset.copy;
      try {
        if (navigator.clipboard && window.isSecureContext)
          await navigator.clipboard.writeText(value);
        else {
          const t = document.createElement("textarea");
          t.value = value;
          document.body.appendChild(t);
          t.select();
          document.execCommand("copy");
          t.remove();
        }
        const old = b.textContent;
        b.textContent = "Đã sao chép";
        setTimeout(() => (b.textContent = old), 1800);
      } catch (e) {
        b.textContent = "Hãy chọn và sao chép";
      }
    }),
  );
  $$("[data-countdown]").forEach((e) => {
    const deadline = Number(e.dataset.countdown);
    const draw = () => {
      const remaining = Math.max(0, deadline - Date.now()),
        seconds = Math.floor(remaining / 1000),
        hours = Math.floor(seconds / 3600);
      e.textContent =
        (hours ? String(hours).padStart(2, "0") + ":" : "") +
        String(Math.floor(seconds / 60) % 60).padStart(2, "0") +
        ":" +
        String(seconds % 60).padStart(2, "0");
      if (!remaining) {
        e.textContent = "Đã hết thời gian";
        e.closest(".countdown")?.classList.add("expired");
        return false;
      }
      return true;
    };
    if (draw()) {
      const timer = setInterval(() => {
        if (!draw()) clearInterval(timer);
      }, 1000);
    }
  });
  $$("[data-history-back]").forEach((b) =>
    b.addEventListener("click", () => {
      if (window.history.length > 1) window.history.back();
      else window.location.assign("/");
    }),
  );
  $$("form").forEach((form) =>
    form.addEventListener("submit", (e) => {
      if (form.dataset.busy === "true") {
        e.preventDefault();
        return;
      }
      if (form.dataset.confirm && !window.confirm(form.dataset.confirm)) {
        e.preventDefault();
        return;
      }
      form.dataset.busy = "true";
      form.classList.add("is-submitting");
      form.setAttribute("aria-busy", "true");
    }),
  );
  window.addEventListener("pageshow", () =>
    $$("form").forEach((f) => {
      delete f.dataset.busy;
      f.classList.remove("is-submitting");
      f.removeAttribute("aria-busy");
    }),
  );
  $$("[data-final-period]").forEach((b) =>
    b.addEventListener("click", () => {
      b.form.elements.period.value = b.dataset.finalPeriod;
    }),
  );
  const estimate = $("[data-invoice-estimate]");
  if (estimate) {
    const form = estimate.closest("form"),
      money = new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      });
    const recalc = () => {
      const n = (name) => Number(form.elements[name]?.value || 0);
      const e =
          Math.max(
            0,
            n("electricity_end") - Number(estimate.dataset.electricityStart),
          ) * Number(estimate.dataset.electricityRate),
        w =
          Math.max(0, n("water_end") - Number(estimate.dataset.waterStart)) *
          Number(estimate.dataset.waterRate);
      estimate.textContent = money.format(
        Math.round(e) +
          Math.round(w) +
          n("service_fee") +
          n("parking_fee") +
          n("adjustment"),
      );
    };
    form.addEventListener("input", recalc);
    recalc();
  }
  $$("[data-image-select]").forEach((s) =>
    s.addEventListener("change", () => {
      const img = $("img", s.closest(".image-field"));
      if (img) img.src = s.dataset.context + s.value;
    }),
  );
})();
