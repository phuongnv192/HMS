package com.module.project.dto.response;

import com.module.project.model.ServiceAddOn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewServiceAddOnResponse {
    private ServiceAddOn parent;
    private List<ServiceAddOn> children;
}
