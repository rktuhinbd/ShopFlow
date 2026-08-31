# UI / UX Design System

ShopFlow's visual language is built on a foundation of **Material 3** but intentionally applies Apple-inspired principles of clarity, restraint, and obsessive visual refinement. The result is a premium, content-first e-commerce experience.

## Design Philosophy

- **Clarity over decoration**: UI elements exist to serve the content, not to distract from it.
- **Restraint**: We avoid gratuitous colors, unnecessary lines, and overly complex layouts.
- **Hierarchy**: Clear visual paths guide the user's eye to the most important actions and information.
- **Consistency**: Interaction patterns and visual weights are predictable across the entire application.

## Core Foundations

### Semantic Color System

ShopFlow uses semantic Material 3 roles rather than hardcoded hex values, mapped strictly to our brand intent:
- **Primary**: Deep teal/emerald (Trust/Commerce). Used for primary actions (Add to Cart).
- **Secondary**: Muted teal. Used for secondary accents and ratings.
- **Tertiary**: Soft coral. Used for destructive actions or specific highlights like Favorites/Sale.
- **Surface**: Clean, neutral backgrounds for content cards.
- **Error**: Clear, standard red for destructive states.

### Typography

We leverage the Material 3 Typography Scale for all text, avoiding arbitrary font sizes. The type ramp is carefully tuned to establish clear hierarchy (e.g., `headlineMedium` for screen titles, `titleMedium` for product names, `bodyMedium` for descriptions).

### Spacing

A strict **4dp baseline grid** is enforced (4dp, 8dp, 12dp, 16dp, 20dp, 24dp, 32dp, 40dp, 48dp). This mathematical precision ensures visual harmony and predictable component sizing.

### Shapes

A constrained shape vocabulary standardizes corner radii:
- Small (4dp)
- Medium (12dp)
- Large (16dp)
- Pill (50px / fully rounded)

### Motion

Transitions and micro-interactions use standard Material 3 motion specifications (emphasized, standard, standard decelerate) to make the interface feel responsive and natural, without being sluggish or overly bouncy.

## Adaptive Layouts

The UI is built responsively using Compose adaptive layout principles, ensuring the experience gracefully scales across different screen sizes and orientations.

## States

Clear visual communication for application states:
- **Loading**: Skeleton screens and subtle progress indicators prevent visual jarring.
- **Error**: Friendly, actionable error states.
- **Empty**: Contextual empty states (e.g., "No favorites yet") with clear next steps.
- **Offline**: Unobtrusive banners or indicators when operating from the local cache.
