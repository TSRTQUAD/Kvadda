% This function plots all the obtained results and calculates the final
% trajectory length and search area to present.

function [trajectorylength,area] = getResults( object, nodes,...
    trajectory, spiraltrajectory, imagelength_meter, plotresult )

% Calculate the total trajectory length
trajectorylength = 0;
for ii = 2:size(trajectory,1)
    trajectorylength = trajectorylength + lldistkm(trajectory(ii-1,:),...
        trajectory(ii,:))*1e3;
end

if object.mission == 1
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                Coordinate Search                 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
if plotresult
radiuslength = lldistkm(spiraltrajectory(1,:),spiraltrajectory(end,:))*1e3;
figure('Name','Coordinate Search','Numbertitle','off'); clf; hold on
plot(trajectory(:,2), trajectory(:,1),'r')
h1 = plot([trajectory(1,2) trajectory(end,2)],...
    [trajectory(1,1) trajectory(end,1)], 'm--');
legend(h1,['Radius: ' num2str(radiuslength) 'm']);
plot_google_map; hold off
end

area = trajectorylength*imagelength_meter;


elseif object.mission == 2
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                   Line Search                    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
if plotresult
figure('Name','Line Search','Numbertitle','off'); clf; hold on
plot(object.area{1}(:,2),object.area{1}(:,1),'k.');
plot(trajectory(:,2), trajectory(:,1),'r');
plot_google_map; hold off
end

area = trajectorylength*imagelength_meter;

elseif object.mission == 3
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                  Area Coverage                   %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Plot and calculate the covered area in m^2
earthellipsoid = referenceSphere('earth','m');
earthellipsoidsurfacearea = areaquad(-90,-180,90,180,earthellipsoid);
if plotresult
figure('Name','Optimal trajectory for area coverage','Numbertitle','off')
clf; hold on; area = 0; forbiddenarea = 0;
for ii = 1:length(object.area)
    area = area + areaint(object.area{ii}(:,1),object.area{ii}(:,2))*...
        earthellipsoidsurfacearea;
    h1 = fill(object.area{ii}(:,2),object.area{ii}(:,1),[0.5,0.5,0.5]);
end
for ii = 1:length(object.forbiddenarea)
    forbiddenarea = forbiddenarea+areaint(object.forbiddenarea{ii}(:,1),...
        object.forbiddenarea{ii}(:,2))*earthellipsoidsurfacearea;
    fill(object.forbiddenarea{ii}(:,2),object.forbiddenarea{ii}(:,1),'w');
end
area = area - forbiddenarea;
plot(nodes(:,2),nodes(:,1), '.k');
h2 = plot(trajectory(:,2),trajectory(:,1),'r');
legend([h1 h2],{['Total area search: ' num2str(area) 'm^2'],...
    ['Total trajectory length: ' num2str(trajectorylength) 'm']})
plot_google_map; hold off
else
area = 0; forbiddenarea = 0;
for ii = 1:length(object.area)
    area = area + areaint(object.area{ii}(:,1),object.area{ii}(:,2))*...
        earthellipsoidsurfacearea;
end
for ii = 1:length(object.forbiddenarea)
    forbiddenarea = forbiddenarea+areaint(object.forbiddenarea{ii}(:,1),...
        object.forbiddenarea{ii}(:,2))*earthellipsoidsurfacearea;
end
area = area - forbiddenarea;
end

end